/*-------------------------------------------------------------------------------
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-16 Scripps Institute (USA) - Dr. Benjamin Good
 *                       STAR Informatics / Delphinai Corporation (Canada) - Dr. Richard Bruskiewich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *-------------------------------------------------------------------------------
 */
package bio.knowledge.database.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import bio.knowledge.model.neo4j.Neo4jReference;

/**
 * @author Richard
 *
 */
public interface ReferenceRepository extends Neo4jRepository<Neo4jReference,Long> {

	/**
	 * 
	 */
	@Query( "DROP CONSTRAINT ON (reference:Reference)"
		  + " ASSERT reference.pmid IS UNIQUE")
	void dropUniqueConstraintOnReferencePmid();

	/**
	 * 
	 */
	@Query( "DROP INDEX ON :Reference(pmid)")
	void dropIndexOnReferencePmid();

	/**
	 * 
	 * @param pmid String PubMed identifier
	 * @return matching Reference
	 */
	@Query( "MATCH (reference:Reference)"
			+ " WHERE reference.pmid = {pmid}"
			+ " RETURN reference")
	Neo4jReference findByPmid(  @Param("pmid") String pmid ) ;

	/**
	 * @param uri
	 * @return
	 */
	Neo4jReference findByUri(String uri);
}
