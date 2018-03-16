/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.mifos.individuallending.internal.service;

import io.mifos.core.lang.ServiceException;
import io.mifos.individuallending.api.v1.domain.caseinstance.CaseCustomerDocuments;
import io.mifos.individuallending.internal.mapper.CaseCustomerDocumentsMapper;
import io.mifos.individuallending.internal.repository.CaseCustomerDocumentEntity;
import io.mifos.individuallending.internal.repository.CaseCustomerDocumentsRepository;
import io.mifos.individuallending.internal.repository.CaseParametersEntity;
import io.mifos.individuallending.internal.repository.CaseParametersRepository;
import io.mifos.portfolio.service.internal.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * @author Myrle Krantz
 */
@Service
public class CaseDocumentsService {
  final private CaseCustomerDocumentsRepository caseCustomerDocumentsRepository;
  final private CaseRepository caseRepository;
  final private CaseParametersRepository caseParametersRepository;

  @Autowired
  public CaseDocumentsService(
      final CaseCustomerDocumentsRepository caseCustomerDocumentsRepository,
      final CaseRepository caseRepository,
      final CaseParametersRepository caseParametersRepository) {
    this.caseCustomerDocumentsRepository = caseCustomerDocumentsRepository;
    this.caseRepository = caseRepository;
    this.caseParametersRepository = caseParametersRepository;
  }

  public Stream<CaseCustomerDocuments.Document> find(
      final String productIdentifier,
      final String caseIdentifier) {
    final CaseParametersEntity caseparametersEntity =
        caseRepository.findByProductIdentifierAndIdentifier(productIdentifier, caseIdentifier)
            .flatMap(x -> caseParametersRepository.findByCaseId(x.getId()))
            .orElseThrow(() -> ServiceException.notFound("Case ''{0}.{1}'' not found", productIdentifier, caseIdentifier));

    return caseCustomerDocumentsRepository.findByCaseParametersId(caseparametersEntity.getId())
        .sorted(Comparator.comparing(CaseCustomerDocumentEntity::getOrder))
        .map(CaseCustomerDocumentsMapper::map);
  }
}