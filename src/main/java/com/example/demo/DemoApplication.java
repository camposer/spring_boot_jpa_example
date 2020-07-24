package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

@RestController
class TheController {
	private PetService service;

	public TheController(PetService service) {
		this.service = service;
	}

	@RequestMapping(value = "/pets", method = RequestMethod.GET)
	public List<Pet> getPets() {
		return service.getPets();
	}

	@RequestMapping(value = "/init", method = RequestMethod.GET)
	public ResponseEntity init() {
		service.insertSomePets();
		return ResponseEntity.ok().build();
	}
}

@Service
class PetService {
	private PetRepository repo;

	public PetService(PetRepository repo) {
		this.repo = repo;
	}

	public List<Pet> getPets() {
		List<Pet> pets = new ArrayList<>();
		repo.findAll().forEach(pets::add);
		return pets;
	}

	@Transactional
	public void insertSomePets() {
		repo.insertSomePets();
	}
}

interface PetRepository extends CrudRepository<Pet, Long>, PetRepositoryCustom {
}

interface PetRepositoryCustom {
	void insertSomePets();
}

class PetRepositoryCustomImpl implements PetRepositoryCustom {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertSomePets() {
		Owner reddy = entityManager.merge(new Owner("reddy"));
		entityManager.persist(new Pet("tina", reddy));
		Owner mathan = entityManager.merge(new Owner("mathan"));
		entityManager.persist(new Pet("nemo", mathan));
	}
}

@Entity
class Pet {
	@Id
	@GeneratedValue
	private Long id;
	private String name;

	@ManyToOne
	private Owner owner;

	public Pet(String name) {
		this.name = name;
	}

	public Pet(String name, Owner owner) {
		this.name = name;
		this.owner = owner;
	}

	public Pet() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}
}

@Entity
class Owner {
	@Id
	@GeneratedValue
	private Long id;
	private String name;

	@OneToMany(mappedBy = "owner")
	private List<Pet> pets;

	public Owner(String name) {
		this.name = name;
	}

	public Owner() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
