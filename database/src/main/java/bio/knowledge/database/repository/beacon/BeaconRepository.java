package bio.knowledge.database.repository.beacon;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import bio.knowledge.model.beacon.neo4j.Neo4jKnowledgeBeacon;

public interface BeaconRepository extends Neo4jRepository<Neo4jKnowledgeBeacon,Long> {

	@Query("MATCH (beacon:Beacon) RETURN beacon")
	List<Neo4jKnowledgeBeacon> findAllBeacons();

	Neo4jKnowledgeBeacon findByUri(String url);
}
