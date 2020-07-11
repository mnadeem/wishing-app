package com.github.mnadeem.wishing.service;

import java.time.LocalDate;
import java.util.function.BiConsumer;

import com.github.mnadeem.wishing.service.data.Wish;
import com.github.mnadeem.wishing.service.support.WishData;

public interface WishingDataService {

	void add(WishData wish);	
	int forEach(LocalDate date, BiConsumer<Wish, LocalDate> wish);
}
