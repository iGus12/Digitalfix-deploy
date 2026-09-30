package com.digitalfix.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digitalfix.catalog.entity.CatalogItem;

public interface CatalogItemRepository extends JpaRepository<CatalogItem, Long> {
}