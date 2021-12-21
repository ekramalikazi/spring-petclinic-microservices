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
package org.springframework.samples.petclinic.customers.web;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.applicaton.ThirdPartyServiceClient;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Szarlinski
 */
@RequestMapping("/owners")
@RestController
@Timed("petclinic.owner")
@RequiredArgsConstructor
@Slf4j
@Validated
class OwnerResource {

	private final OwnerRepository ownerRepository;

	private final ThirdPartyServiceClient thirdPartyServiceClient;

	/**
	 * Create Owner
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create a new Owner")
	public Owner createOwner(@Valid @RequestBody Owner owner) {
		return ownerRepository.save(owner);
	}

	/**
	 * Read single Owner
	 */
	@GetMapping(value = "/{ownerId}")
	@Operation(summary = "Get Owner object by its id")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "Found the Owner",
							content = { @Content(mediaType = "application/json",
									schema = @Schema(implementation = Owner.class)) }),
					@ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
					@ApiResponse(responseCode = "404", description = "Owner not found", content = @Content) })
	public Optional<Owner> findOwner(@PathVariable("ownerId") int ownerId) {
		String results = thirdPartyServiceClient.getExternalService();
		log.info("External call result = {} ", results);
		return ownerRepository.findById(ownerId);
	}

	/**
	 * Read List of Owners
	 */
	@GetMapping
	@Operation(summary = "Get all Owners")
	public List<Owner> findAll() {
		return ownerRepository.findAll();
	}

	/**
	 * Update Owner
	 */
	@PutMapping(value = "/{ownerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Update a Owner by its id")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "successful operation"),
			@ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
			@ApiResponse(responseCode = "404", description = "Owner not found"),
			@ApiResponse(responseCode = "405", description = "Validation exception") })
	public void updateOwner(@PathVariable("ownerId") int ownerId, @Valid @RequestBody Owner ownerRequest) {
		final Optional<Owner> owner = ownerRepository.findById(ownerId);

		final Owner ownerModel = owner
				.orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " not found"));
		// This is done by hand for simplicity purpose. In a real life use-case we should
		// consider using MapStruct.
		ownerModel.setFirstName(ownerRequest.getFirstName());
		ownerModel.setLastName(ownerRequest.getLastName());
		ownerModel.setCity(ownerRequest.getCity());
		ownerModel.setAddress(ownerRequest.getAddress());
		ownerModel.setTelephone(ownerRequest.getTelephone());
		log.info("Saving owner {}", ownerModel);
		ownerRepository.save(ownerModel);
	}

}
