package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.caixa.DTO.RequestSimulacaoDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class RestTest {
    // @Test
    // void testSimularEndpoint() {
    //     RequestSimulacaoDTO request = new RequestSimulacaoDTO();
    //     request.setValorDesejado(new BigDecimal("1000"));
    //     request.setPrazo(20);

    //     given()
    //             .contentType("application/json")
    //             .body(request)
    //             .when()
    //             .get("/api/simular")
    //             .then()
    //             .statusCode(200)
    //             .body("idSimulacao", notNullValue())
    //             .body("codigoProduto", equalTo(1))
    //             .body("resultadoSimulacao", notNullValue());
    // }

}