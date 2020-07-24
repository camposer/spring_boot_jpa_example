package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
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
	private final PetService petService;
	private final OwnerService ownerService;

	public TheController(PetService petService, OwnerService ownerService) {
		this.petService = petService;
		this.ownerService = ownerService;
	}

	@RequestMapping(value = "/pets", method = RequestMethod.GET)
	public List<Pet> getPets() {
		return petService.getPets();
	}

	@RequestMapping(value = "/init", method = RequestMethod.GET)
	public ResponseEntity init() {
		petService.insertSomePets();
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/owners/{id}", method = RequestMethod.DELETE)
	public ResponseEntity delete(@PathVariable("id") Long id) {
		ownerService.delete(id);
		return ResponseEntity.ok().build();
	}
}

@Service
class PetService {
	private PetRepository repo;

	public PetService(PetRepository repo) {
		this.repo = repo;
	}

	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
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

@Service
@Transactional
class OwnerService {
	private final PetRepository petRepository;
	private final OwnerRepository ownerRepository;

	public OwnerService(PetRepository petRepository, OwnerRepository ownerRepository) {
		this.petRepository = petRepository;
		this.ownerRepository = ownerRepository;
	}

	public void delete(Long id) {
		petRepository.deleteAllByOwnerId(id);
		ownerRepository.deleteById(id);
	}
}

interface OwnerRepository extends CrudRepository<Owner, Long> {
}

interface PetRepository extends CrudRepository<Pet, Long>, PetRepositoryCustom {
	@Query("delete from Pet p where p.owner.id = :ownerId")
	@Modifying
	void deleteAllByOwnerId(@Param("ownerId") Long ownerId);
}

interface PetRepositoryCustom {
	void insertSomePets();
}

class PetRepositoryCustomImpl implements PetRepositoryCustom {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void insertSomePets() {
		entityManager.persist(new Owner(1L,"reddy"));
		entityManager.persist(new Pet("tina", entityManager.find(Owner.class, 1L)));
//		entityManager.persist(new Owner(1L, "mathan")); // it should fail (because the pk is duplicated)!!
//		entityManager.persist(new Pet("nemo", entityManager.find(Owner.class, 1L)));
		entityManager.persist(new Owner(2L, "mathan"));
		entityManager.persist(new Pet("nemo", entityManager.find(Owner.class, 2L)));
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
	private Long id;
	private String name;

	@OneToMany(mappedBy = "owner")
	private List<Pet> pets;

	public Owner(String name) {
		this.name = name;
	}

    public Owner(Long id, String name) {
        this.name = name;
        this.id = id;
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
