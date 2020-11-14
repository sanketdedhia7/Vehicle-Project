package mitchell.vehicleProject.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import mitchell.vehicleProject.entity.Vehicle;
import mitchell.vehicleProject.service.VehicleService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Log4j2
public class VehicleControllerTest {

  @Autowired(required = true)
  private MockMvc mockMvc;

  @InjectMocks private VehicleController vehicleController;

  @Mock private VehicleService vehicleService;

  @Autowired private ObjectMapper objectMapper;

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
  }

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
  public void shouldFetchAllVehicles() throws Exception {

    when(vehicleService.getVehicles(Optional.empty(), Optional.empty(), Optional.empty()))
        .thenReturn(getVehicles());
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/vehicles");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    String expected =
        "[{\"id\":1,\"year\":2000,\"make\":\"honda\",\"model\":\"civic\"},{\"id\":2,\"year\":2001,\"make\":\"honda\",\"model\":\"accord\"},{\"id\":3,\"year\":2002,\"make\":\"BMW\",\"model\":\"i8\"},{\"id\":4,\"year\":2003,\"make\":\"Tesla\",\"model\":\"model X\"}]";
    String actual = result.getResponse().getContentAsString();
    assertThat(actual, comparesEqualTo(expected));
  }

  @Test
  public void shouldFetchOneVehicleById() throws Exception {
    when(vehicleService.getVehicle(1)).thenReturn(getVehicles().get(0));
    RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/vehicles/1");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    String expected = "{\"id\":1,\"year\":2000,\"make\":\"honda\",\"model\":\"civic\"}";
    String actual = result.getResponse().getContentAsString();
    assertThat(actual, comparesEqualTo(expected));
  }

  @Test
  public void shouldDeleteVehicle() throws Exception {
    doNothing().when(vehicleService).deleteVehicle(1);
    RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/vehicles/1");
    MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    String expected = "vehicle deleted";
    String actual = result.getResponse().getContentAsString();
    assertThat(actual, comparesEqualTo(expected));
  }

  @Test
  public void shouldCreateNewVehicle() throws Exception {

    doNothing().when(vehicleService).createVehicle(getVehicles().get(0));

    MvcResult result =
        this.mockMvc
            .perform(
                post("/vehicles")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(result(getVehicles().get(0))))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isCreated())
            .andReturn();
    assertThat(result.getResponse().getContentAsString(), comparesEqualTo("vehicle created"));
  }

  @Test
  public void shouldUpdateExistingVehicle() throws Exception {

    doNothing().when(vehicleService).createVehicle(getVehicles().get(0));

    MvcResult result =
        this.mockMvc
            .perform(
                put("/vehicles")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(result(getVehicles().get(1))))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andReturn();
    assertThat(result.getResponse().getContentAsString(), comparesEqualTo("vehicle updated"));
  }

  String result(Object object) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(object);
  }
}
