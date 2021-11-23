/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.customers.applicaton;

import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.List;

/**
 * @author Maciej Szarlinski
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ThirdPartyServiceClient {

	// Could be changed for testing purpose
	private String hostname = "http://thirdparty-service/";

	private final WebClient.Builder webClientBuilder;

	public String getExternalService() {
		log.info("hello");
		// HttpClient reactorHttpClient =
		// HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
		String body = webClientBuilder
				// .clientConnector(new ReactorClientHttpConnector(reactorHttpClient))
				.build().get().uri(hostname + "external").retrieve().bodyToMono(String.class).block();
		log.info("world");
		return body;
	}

}
