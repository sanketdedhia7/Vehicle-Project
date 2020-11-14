package mitchell.vehicleProject.controller;

import mitchell.vehicleProject.entity.Vehicle;
import mitchell.vehicleProject.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class VehicleController {

  @Autowired
  private VehicleService vehicleService;

  @PostMapping(value = "/vehicles", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> createVehicle(@RequestBody Vehicle vehicle) {
    vehicleService.createVehicle(vehicle);
    return new ResponseEntity<>("vehicle created", HttpStatus.CREATED);
  }

  @GetMapping(value = "/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<Vehicle>> getVehicles(
      @RequestParam Optional<Integer> id,
      @RequestParam Optional<Integer> year,
      @RequestParam Optional<String> make,
      @RequestParam Optional<String> model) {
    List<Vehicle> vehicles = vehicleService.getVehicles(year, make, model);
    return new ResponseEntity<>(vehicles, HttpStatus.OK);
  }

  @GetMapping(value = "/vehicles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Vehicle> getVehicle(@PathVariable Integer id) {
    Vehicle vehicle = vehicleService.getVehicle(id);
    return new ResponseEntity<>(vehicle, HttpStatus.OK);
  }

  @PutMapping(value = "/vehicles", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateVehicle(@RequestBody Vehicle vehicle) {
    vehicleService.updateVehicle(vehicle);
    return new ResponseEntity<>("vehicle updated", HttpStatus.OK);
  }

  @DeleteMapping(value = "/vehicles/{id}")
  public ResponseEntity<String> deleteVehicle(@PathVariable Integer id) {
    vehicleService.deleteVehicle(id);
    return new ResponseEntity<>("vehicle deleted", HttpStatus.NO_CONTENT);
  }
}
