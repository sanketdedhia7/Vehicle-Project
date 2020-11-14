package mitchell.vehicleProject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import mitchell.vehicleProject.entity.Vehicle;
import mitchell.vehicleProject.exceptions.BadRequestException;
import mitchell.vehicleProject.repository.VehicleRepository;
import org.apache.logging.log4j.util.Strings;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@Log4j2
public class VehicleServiceTest {

  @InjectMocks
  private VehicleService vehicleService;

  @Mock
  private VehicleRepository vehicleRepository;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private List<Vehicle> getVehicles() {
    List<Vehicle> vehicles = new ArrayList<>();
    try {
      vehicles =
          new ObjectMapper()
              .readValue(
                  new String(
                      Files.readAllBytes(
                          Paths.get("src/test/java/mitchell/vehicleProject/entity/Vehicle.json"))),
                  new TypeReference<List<Vehicle>>() {});
    } catch (Exception e) {
       // log.info(e);
    }
    return vehicles;
  }

  @Test
  public void givenVehicleMakeIsNullWhenCreateVehicleThrowBadRequestException() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Make or Model cannot be null");
    Vehicle vehicle = getVehicles().get(0);
    vehicle.setMake(null);
    vehicleService.createVehicle(vehicle);
  }

  @Test
  public void givenVehicleMakeIsEmptyWhenCreateVehicleThrowBadRequestException() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Make or Model cannot be empty");
    Vehicle vehicle = getVehicles().get(0);
    vehicle.setMake(Strings.EMPTY);
    vehicleService.createVehicle(vehicle);
  }

  @Test
  public void givenVehicleModelIsNullWhenCreateVehicleThrowBadRequestException() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Make or Model cannot be null");
    Vehicle vehicle = getVehicles().get(0);
    vehicle.setModel(null);
    vehicleService.createVehicle(vehicle);
  }

  @Test
  public void givenVehicleModelIsEmptyWhenCreateVehicleThrowBadRequestException() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Make or Model cannot be empty");
    Vehicle vehicle = getVehicles().get(0);
    vehicle.setModel(Strings.EMPTY);
    vehicleService.createVehicle(vehicle);
  }

  @Test
  public void givenVehicleYearIsGreaterThan2050WhenCreateVehicleThrowBadRequestException() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Year must be between 1950 and 2050");
    Vehicle vehicle = getVehicles().get(0);
    vehicle.setYear(2051);
    vehicleService.createVehicle(vehicle);
  }

  @Test
  public void givenVehicleYearIsLessThan1950WhenCreateVehicleThrowBadRequestException() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Year must be between 1950 and 2050");
    Vehicle vehicle = getVehicles().get(0);
    vehicle.setYear(1949);
    vehicleService.createVehicle(vehicle);
  }

  @Captor ArgumentCaptor<Vehicle> vehicleArgumentCaptor;

  @Test
  public void givenVehicleWhenCreateVehicleThenSaveVehicle() {
    vehicleService.createVehicle(getVehicles().get(0));
    verify(vehicleRepository, times(1)).save(vehicleArgumentCaptor.capture());
    Vehicle actualVehicle = vehicleArgumentCaptor.getValue();
    assertThat(actualVehicle.getYear(), comparesEqualTo(2000));
    assertThat(actualVehicle.getMake(), comparesEqualTo("honda"));
    assertThat(actualVehicle.getModel(), comparesEqualTo("civic"));
  }

  /** ************Get Vehicle By Id ********************** */
  @Test
  public void
      givenVehicleIdNotPresentInTheDatabaseWhenGetVehicleIsCallThenThrowBadRequestException() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Vehicle with id 1000 is not present in the database");
    when(vehicleRepository.findById(anyInt())).thenReturn(Optional.empty());
    vehicleService.getVehicle(1000);
  }

  @Test
  public void givenVehicleIdWhenGetVehicleIsCallThenReturnVehicle() {
    when(vehicleRepository.findById(anyInt())).thenReturn(Optional.of(getVehicles().get(0)));
    Vehicle actualVehicle = vehicleService.getVehicle(1);
    assertThat(actualVehicle.getId(), comparesEqualTo(1));
    assertThat(actualVehicle.getYear(), comparesEqualTo(2000));
    assertThat(actualVehicle.getMake(), comparesEqualTo("honda"));
    assertThat(actualVehicle.getModel(), comparesEqualTo("civic"));
  }

  /** ************Get All Vehicles********************** */
  @Test
  public void givenNoFiltersWhenGetAllVehiclesThenReturnListOfVehicles() {
    when(vehicleRepository.findAll()).thenReturn(getVehicles());
    List<Vehicle> actualVehicles =
        vehicleService.getVehicles(Optional.empty(), Optional.empty(), Optional.empty());
    assertThat(actualVehicles.size(), comparesEqualTo(4));
    assertThat(actualVehicles.get(0).getId(), comparesEqualTo(1));
    assertThat(actualVehicles.get(3).getId(), comparesEqualTo(4));
  }

  @Test
  public void givenYearFilterWhenGetAllVehiclesThenReturnListOfVehiclesOfAskedYear() {
    when(vehicleRepository.findAll()).thenReturn(getVehicles());
    List<Vehicle> actualVehicles =
        vehicleService.getVehicles(Optional.of(2000), Optional.empty(), Optional.empty());
    assertThat(actualVehicles.size(), comparesEqualTo(1));
    assertThat(actualVehicles.get(0).getId(), comparesEqualTo(1));
  }

  @Test
  public void givenMakeFilterWhenGetAllVehiclesThenReturnListOfVehiclesOfAskedMake() {
    when(vehicleRepository.findAll()).thenReturn(getVehicles());
    List<Vehicle> actualVehicles =
        vehicleService.getVehicles(Optional.empty(), Optional.of("honda"), Optional.empty());
    assertThat(actualVehicles.size(), comparesEqualTo(2));
    assertThat(actualVehicles.get(0).getId(), comparesEqualTo(1));
    assertThat(actualVehicles.get(1).getId(), comparesEqualTo(2));
  }

  @Test
  public void givenModelFilterWhenGetAllVehiclesThenReturnListOfVehiclesOfAskedModel() {
    when(vehicleRepository.findAll()).thenReturn(getVehicles());
    List<Vehicle> actualVehicles =
        vehicleService.getVehicles(Optional.empty(), Optional.empty(), Optional.of("accord"));
    assertThat(actualVehicles.size(), comparesEqualTo(1));
    assertThat(actualVehicles.get(0).getId(), comparesEqualTo(2));
  }

  /** ************Delete Vehicle By Id********************** */
  @Test
  public void givenVehicleIdWhenDeleteVehicleThenDeleteFromDatabase() {
    doNothing().when(vehicleRepository).deleteById(anyInt());
    vehicleService.deleteVehicle(1);
    verify(vehicleRepository, times(1)).deleteById(1);
  }

  /** ************Update Vehicle API********************** */
  @Test
  public void givenUpdatedVehicleWhenUpdateVehicleThenSaveUpdatedVehicle() {
    when(vehicleRepository.findById(anyInt())).thenReturn(Optional.of(getVehicles().get(0)));
    vehicleService.updateVehicle(getVehicles().get(3));
    verify(vehicleRepository, times(1)).save(vehicleArgumentCaptor.capture());
    Vehicle updatedVehicle = vehicleArgumentCaptor.getValue();
    assertThat(updatedVehicle.getYear(), comparesEqualTo(2003));
    assertThat(updatedVehicle.getMake(), comparesEqualTo("Tesla"));
    assertThat(updatedVehicle.getModel(), comparesEqualTo("model X"));
  }
}
