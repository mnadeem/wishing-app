package com.github.mnadeem.wishing.service;

import java.time.LocalDate;
import java.util.function.Consumer;

import com.github.mnadeem.wishing.service.data.Wish;
import com.github.mnadeem.wishing.service.support.WishData;

public interface WishingDataService {

	void add(WishData wish);	
	void forEach(LocalDate date, Consumer<Wish> wish);
}
