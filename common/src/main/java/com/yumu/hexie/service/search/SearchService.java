package com.yumu.hexie.service.search;

import java.util.Set;

public interface SearchService {

	void save(String searchKey, String searchValue);

	Set<String> get(String searchKey);

	void removeAll(String searchKey);

}
