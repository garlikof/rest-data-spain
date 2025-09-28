package org.garlikoff.restdata.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RealEstateMilvusServiceTest {

    private final RealEstateMilvusService service = new RealEstateMilvusService();

    @Test
    void skipsExtraParamsWhenIndexTypeIsUnavailable() {
        MockBuilder builder = new MockBuilder();

        service.createCollection(builder, "HNSW", "{\"metric_type\":\"L2\"}");

        assertThat(builder.extraParamInvocations).isZero();
    }

    private static class MockBuilder {
        private int extraParamInvocations = 0;

        public MockBuilder withExtraParam(String extraParams) {
            extraParamInvocations++;
            return this;
        }
    }
}
