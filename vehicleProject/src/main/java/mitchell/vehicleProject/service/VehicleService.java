package mitchell.vehicleProject.service;

import lombok.extern.log4j.Log4j2;
import mitchell.vehicleProject.entity.Vehicle;
import mitchell.vehicleProject.exceptions.BadRequestException;
import mitchell.vehicleProject.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
public class VehicleService {

  @Autowired
  private VehicleRepository vehicleRepository;

  private void checkVehicleRequest(Vehicle vehicle) {
    if (Objects.isNull(vehicle.getMake()) || Objects.isNull(vehicle.getModel())) {
      throw new BadRequestException("Make or Model cannot be null");
    }
    if (vehicle.getMake().isEmpty() || vehicle.getModel().isEmpty()) {
      throw new BadRequestException("Make or Model cannot be empty");
    }
    if (vehicle.getYear() > 2050 || vehicle.getYear() < 1950) {
      throw new BadRequestException("Year must be between 1950 and 2050");
    }
  }

  public void createVehicle(Vehicle vehicle) {
    checkVehicleRequest(vehicle);
    Vehicle savedObject =  vehicleRepository.save(vehicle);
  }

  public List<Vehicle> getVehicles(
      Optional<Integer> year, Optional<String> make, Optional<String> model) {

    Integer vehicleYear = year.orElse(null);
    String vehicleMake = make.orElse(null);
    String vehicleModel = model.orElse(null);
    List<Vehicle> vehicles = vehicleRepository.findAll();
    if (Objects.isNull(vehicleYear)
        && Objects.isNull(vehicleMake)
        && Objects.isNull(vehicleModel)) {
      return vehicles;
    }
    if (Objects.nonNull(vehicleYear)) {
      vehicles =
          vehicles.stream()
              .filter(vehicle -> vehicleYear.equals(vehicle.getYear()))
              .collect(Collectors.toList());
    }
    if (Objects.nonNull(vehicleMake)) {
      vehicles =
          vehicles.stream()
              .filter(vehicle -> vehicleMake.equals(vehicle.getMake()))
              .collect(Collectors.toList());
    }
    if (Objects.nonNull(vehicleModel)) {
      vehicles =
          vehicles.stream()
              .filter(vehicle -> vehicleModel.equals(vehicle.getModel()))
              .collect(Collectors.toList());
    }
    return vehicles;
  }

  public Vehicle getVehicle(Integer id) {
    Optional<Vehicle> optionalVehicle = vehicleRepository.findById(id);
    if (optionalVehicle.isPresent()) {
      return optionalVehicle.get();
    }
    throw new BadRequestException(
        String.format("Vehicle with id %d is not present in the database", id));
  }

  public void updateVehicle(Vehicle vehicle) {
    Vehicle existingVehicle = getVehicle(vehicle.getId());
    checkVehicleRequest(vehicle);
    existingVehicle.setMake(vehicle.getMake());
    existingVehicle.setModel(vehicle.getModel());
    existingVehicle.setYear(vehicle.getYear());
    vehicleRepository.save(existingVehicle);
  }

  public void deleteVehicle(Integer id) {
    vehicleRepository.deleteById(id);
  }
}
