package jsprit.examples;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jsprit.analysis.toolbox.GraphStreamViewer;
import jsprit.analysis.toolbox.Plotter;
import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.SchrimpfFactory;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.io.VrpXMLWriter;
import jsprit.core.problem.job.Shipment;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import jsprit.core.reporting.SolutionPrinter;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;
import jsprit.util.Examples;

public class SimpleEnRoutePickupAndDeliveryExample {

    public static void main(String[] args) {
        /*
         * some preparation - create output folder
         */
        Examples.createOutputFolder();

        /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and a capacity of 2
         */
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType").addCapacityDimension(0, 2);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        /*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
         */
        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(loc(Coordinate.newInstance(10, 10)));
        vehicleBuilder.setType(vehicleType);
        VehicleImpl vehicle = vehicleBuilder.build();

        try (FileReader reader = new FileReader("C://Users//souhail//Desktop//IRISTI//NYCData_00h00_cluster_2.json")) {
            JsonArray orders = JsonParser.parseReader(reader).getAsJsonArray();

            List<Shipment> shipments = new ArrayList<>();

            for (int i = 0; i < orders.size(); i++) {
                JsonObject order = orders.get(i).getAsJsonObject();
                String id = order.getAsJsonPrimitive("id").getAsString();
                String idNumber = id.replaceAll("[^0-9]", "");

                double pickUpLat = order.getAsJsonPrimitive("pickLat").getAsDouble();
                double pickUpLng = order.getAsJsonPrimitive("pickLng").getAsDouble();
                double dropOffLat = order.getAsJsonPrimitive("dropLat").getAsDouble();
                double dropOffLng = order.getAsJsonPrimitive("dropLng").getAsDouble();

                System.out.println("PickUp Location: (" + pickUpLat + ", " + pickUpLng + ")");
                System.out.println("DropOff Location: (" + dropOffLat + ", " + dropOffLng + ")");

                Shipment shipment = Shipment.Builder.newInstance(idNumber)
                        .addSizeDimension(0, 1)
                        .setPickupLocation(loc(Coordinate.newInstance(pickUpLat, pickUpLng)))
                        .setDeliveryLocation(loc(Coordinate.newInstance(dropOffLat, dropOffLng)))
                        .build();

                shipments.add(shipment);
            }

            VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
            vrpBuilder.addVehicle(vehicle);
            for (Shipment shipment : shipments) {
                vrpBuilder.addJob(shipment);
            }

            VehicleRoutingProblem problem = vrpBuilder.build();

            /*
             * get the algorithm out-of-the-box. 
             */
            VehicleRoutingAlgorithm algorithm = new SchrimpfFactory().createAlgorithm(problem);

            /*
             * and search a solution
             */
            Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

            /*
             * get the best 
             */
            VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

            /*
             * write out problem and solution to xml-file
             */
            new VrpXMLWriter(problem, solutions).write("C:\\Users\\souhail\\Desktop\\IRISTI\\Output.xml");

            /*
             * print nRoutes and totalCosts of bestSolution
             */
            SolutionPrinter.print(bestSolution);

            /*
             * plot problem without solution
             */
            Plotter problemPlotter = new Plotter(problem);
            problemPlotter.plotShipments(true);
            problemPlotter.plot("output/simpleEnRoutePickupAndDeliveryExample_problem.png", "en-route pickup and delivery");

            /*
             * plot problem with solution
             */
            Plotter solutionPlotter = new Plotter(problem, Arrays.asList(Solutions.bestOf(solutions).getRoutes().iterator().next()));
            solutionPlotter.plotShipments(true);
            solutionPlotter.plot("output/simpleEnRoutePickupAndDeliveryExample_solution.png", "en-route pickup and delivery");

            new GraphStreamViewer(problem).setRenderShipments(true).display();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Location loc(Coordinate coordinate) {
        return Location.Builder.newInstance().setCoordinate(coordinate).build();
    }
}