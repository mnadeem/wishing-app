package com.github.mnadeem.wishing.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.github.mnadeem.wishing.service.data.Wish;
import com.github.mnadeem.wishing.service.data.Wish.WishKey;
import com.github.mnadeem.wishing.service.support.ExcelFile;
import com.github.mnadeem.wishing.service.support.ExcelFileReader;
import com.github.mnadeem.wishing.service.support.ExcelFiles;
import com.github.mnadeem.wishing.service.support.WishData;

@Service
public class DefaultWishingDataService implements WishingDataService {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultWishingDataService.class);

	@Autowired
	private Environment env;
	@Autowired
	private ResourceLoader resourceLoader;

	private MultiValuedMap<WishKey, Wish> data = new HashSetValuedHashMap<WishKey, Wish>();

	@PostConstruct
    public void init() {
		new ExcelFileReader(resourceLoader, buildExcelFiles()).forEach(wishData -> add(wishData));
    }

	private ExcelFiles buildExcelFiles() {
		Integer fileCount = env.<Integer>getProperty("app.count.excel_files", Integer.class, 1);
		logger.debug("Loading {} excel files ", fileCount);
		List<ExcelFile> files = new ArrayList<>();
		for (int i = 0; i < fileCount; i++) {
			files.add(buildExcelFile(i + 1));		
		}
		return new ExcelFiles(files);
	}

	private ExcelFile buildExcelFile(int i) {

		String fileName = env.<String>getProperty("app.name" + i + ".excel_file", String.class, "data/workbook1.xlsx");

		int nameIndex = env.<Integer>getProperty("app.name" + i + ".column", Integer.class, 1);
		int emailIndex = env.<Integer>getProperty("app.email" + i + ".column", Integer.class, 2);
		int dobIndex = env.<Integer>getProperty("app.dob" + i + ".column", Integer.class, 3);
		int hireIndex = env.<Integer>getProperty("app.hire" + i + ".column", Integer.class, 4);
		int sheetNumber = env.<Integer>getProperty("app.number" + i + ".excel_sheet", Integer.class, 1);

		ExcelFile file = new ExcelFile();
		file.setFileName(fileName);
		file.setNameIndex(nameIndex - 1);
		file.setEmailIndex(emailIndex - 1);
		file.setDobIndex(dobIndex - 1);
		file.setHireIndex(hireIndex - 1);
		file.setSheetNumber(sheetNumber - 1);
		file.setWorkbookNumber(i);
		return file;
	}

	@Override
	public void add(WishData wishData) {
		List<Wish> wishes = buildWishes(wishData);
		wishes.forEach(wish -> data.put(wish.getWishKey(), wish));
	}

	private List<Wish> buildWishes(WishData wishData) {
		List<Wish> wishes = new ArrayList<Wish>();
		if (wishData.getBirthDate() != null) {			
			wishes.add(buildBirthdayWish(wishData));		
		}
		if (wishData.getHireDate() != null) {			
			wishes.add(buildAnniversaryWish(wishData));
		}

		return wishes;
	}

	private Wish buildAnniversaryWish(WishData wishData) {
		Wish aWish = Wish.anniversaryWish();
		aWish.setName(wishData.getName());
		aWish.setEmail(wishData.getEmail());
		aWish.setEventDate(wishData.getHireDate());
		aWish.setPartition(wishData.getPartition());
		aWish.setWish("Happy Work Anniversary!");
		aWish.setDetail("");
		return aWish;
	}

	private Wish buildBirthdayWish(WishData wishData) {
		Wish bWish = Wish.birthdayWish();
		bWish.setName(wishData.getName());
		bWish.setEmail(wishData.getEmail());
		bWish.setEventDate(wishData.getBirthDate());
		bWish.setPartition(wishData.getPartition());
		bWish.setWish("Happy Birthday!");
		bWish.setDetail("");
		return bWish;
	}

	@Override
	public void forEach(LocalDate date, Consumer<Wish> wish) {
		WishKey wishKey = new WishKey(date.getMonthValue(), date.getDayOfMonth());
		data.get(wishKey).forEach(wish);
	}
}
