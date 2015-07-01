package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.Visit;

/**
 * Service interface for CRUD on Visit
 */
public interface VisitService {

    Visit create(Visit visit);

    Visit update(Visit visit);

    Visit createOrUpdate(Visit visit);

    void delete(Visit visit);
}
