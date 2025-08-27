package org.caixa;

import org.caixa.Consulta.ConsultaDAO;
import org.caixa.Consulta.ProdutoModel;
import org.caixa.DTO.*;
import org.caixa.Exception.ErroPrevistoException;
import org.caixa.Historico.HistoricoDAO;
import org.caixa.Historico.MetricsModel;
import org.caixa.Metrics.MetricsDAO;
import org.caixa.service.SimulacaoService;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServiceTeste {
    @InjectMocks
    private SimulacaoService simulacaoService;

    @Mock
    private ConsultaDAO consultaDao;

    @Mock
    private HistoricoDAO historicoDao;

    @Mock
    private MetricsDAO metricsDao;

    @Mock
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Test
    void deveRetornarProdutoQuandoDadosValidos() {
        RequestSimulacaoDTO dados = new RequestSimulacaoDTO();
        dados.setPrazo(12);
        dados.setValorDesejado(BigDecimal.valueOf(10000));

        ProdutoModel produtoEsperado = new ProdutoModel();
        produtoEsperado.setId(1);
        produtoEsperado.setDescricao("Produto Teste");

        when(consultaDao.getProduto(12, BigDecimal.valueOf(10000)))
                .thenReturn(produtoEsperado);

        ProdutoModel resultado = simulacaoService.obterDadosProduto(dados);

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getId());
        Assertions.assertEquals("Produto Teste", resultado.getDescricao());
    }

    @Test
    void deveLancarErroQuandoDadosNulos() {
        Assertions.assertThrows(ErroPrevistoException.class, () -> {
            simulacaoService.obterDadosProduto(null);
        });
    }

    @Test
    void deveLancarErroQuandoPrazoMenorOuIgualZero() {
        RequestSimulacaoDTO dados = new RequestSimulacaoDTO();
        dados.setPrazo(0);
        dados.setValorDesejado(BigDecimal.valueOf(10000));

       Assertions.assertThrows(ErroPrevistoException.class, () -> {
            simulacaoService.obterDadosProduto(dados);
        });
    }

    @Test
    void deveLancarErroQuandoValorDesejadoMenorOuIgualZero() {
        RequestSimulacaoDTO dados = new RequestSimulacaoDTO();
        dados.setPrazo(12);
        dados.setValorDesejado(BigDecimal.ZERO);

        Assertions.assertThrows(ErroPrevistoException.class, () -> {
            simulacaoService.obterDadosProduto(dados);
        });
    }

    @Test
    void deveCalcularSACCorretamente() {
        RequestSimulacaoDTO dados = new RequestSimulacaoDTO();
        dados.setPrazo(2);
        dados.setValorDesejado(BigDecimal.valueOf(1000));

        BigDecimal taxa = BigDecimal.valueOf(0.01); // 1%

        TransferDTO resultado = simulacaoService.calcularSAC(dados, taxa);

        Assertions.assertEquals("SAC", resultado.getSistema());
        Assertions.assertEquals(2, resultado.getParcelas().size());

        // Verifica se o valor total pago está correto
        BigDecimal valorEsperado = resultado.getParcelas().stream()
                .map(ParcelaDTO::getValorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assertions.assertEquals(valorEsperado, resultado.getValorTotal());
    }

    @Test
    void deveLancarErroQuandoTaxaInvalidaSAC() {
        RequestSimulacaoDTO dados = new RequestSimulacaoDTO();
        dados.setPrazo(2);
        dados.setValorDesejado(BigDecimal.valueOf(1000));

        BigDecimal taxa = BigDecimal.ZERO;

        ErroPrevistoException ex = Assertions.assertThrows(ErroPrevistoException.class, () -> {
            simulacaoService.calcularSAC(dados, taxa);
        });

        Assertions.assertEquals("A taxa obtida possui um valor inválido, contate um administrador", ex.mensagem.getMensagem());
    }

    @Test
    void deveCalcularPRICECorretamente() {
        RequestSimulacaoDTO dados = new RequestSimulacaoDTO();
        dados.setPrazo(2);
        dados.setValorDesejado(BigDecimal.valueOf(1000));

        BigDecimal taxa = BigDecimal.valueOf(0.01); // 1%

        TransferDTO resultado = simulacaoService.calcularPRICE(dados, taxa);

        Assertions.assertEquals("PRICE", resultado.getSistema());
        Assertions.assertEquals(2, resultado.getParcelas().size());

        // Verifica se o valor total pago está correto
        BigDecimal valorEsperado = resultado.getParcelas().stream()
                .map(ParcelaDTO::getValorPrestacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Assertions.assertEquals(valorEsperado, resultado.getValorTotal());
    }

    @Test
    void deveLancarErroQuandoTaxaInvalidaPRICE() {
        RequestSimulacaoDTO dados = new RequestSimulacaoDTO();
        dados.setPrazo(2);
        dados.setValorDesejado(BigDecimal.valueOf(1000));

        BigDecimal taxa = BigDecimal.ZERO;

        ErroPrevistoException ex = Assertions.assertThrows(ErroPrevistoException.class, () -> {
            simulacaoService.calcularPRICE(dados, taxa);
        });

        Assertions.assertEquals("A taxa obtida possui um valor inválido, contate um administrador", ex.mensagem.getMensagem());
    }


    @Test
    void deveLancarErroQuandoNaoExistemProdutos() {
        when(consultaDao.getProdutos()).thenReturn(Collections.emptyList());

        ErroPrevistoException ex = Assertions.assertThrows(ErroPrevistoException.class, () -> {
            simulacaoService.obterSimulacoesPorDia("27/08/2025");
        });

        Assertions.assertEquals("Nenhum produto encontrado.", ex.mensagem.getMensagem());
    }

    @Test
    void deveRetornarSimulacoesPorDiaComDadosValidos() {
        ProdutoModel produto = new ProdutoModel();
        produto.setId(1);
        produto.setDescricao("Produto Teste");
        produto.setJuros(BigDecimal.valueOf(0.02));

        RegistroDTO registro = new RegistroDTO();
        registro.setPrazo(10);
        registro.setValorDesejado(BigDecimal.valueOf(1000));
        registro.setValorTotalParcelas(BigDecimal.valueOf(1200));

        when(consultaDao.getProdutos()).thenReturn(List.of(produto));
        when(historicoDao.getSimulacoesPorDiaPorProduto(any(), eq(1)))
                .thenReturn(List.of(registro));

        SimulacoesPorDataDTO resultado = simulacaoService.obterSimulacoesPorDia("27/08/2025");

        Assertions.assertNotNull(resultado);
        Assertions.assertEquals(1, resultado.getSimulacoes().size());
        VolumeDTO volume = resultado.getSimulacoes().get(0);
        Assertions.assertEquals(produto.getId(), volume.getCodigoProduto());
        Assertions.assertEquals(produto.getDescricao(), volume.getDescricaoProduto());
    }

}
