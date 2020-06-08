package com.github.mnadeem.wishing.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.util.StringUtils;

import com.github.mnadeem.wishing.service.data.Wish;
import com.github.mnadeem.wishing.service.data.Wish.WishKey;
import com.github.mnadeem.wishing.service.support.ExcelFile;
import com.github.mnadeem.wishing.service.support.ExcelFileReadError;
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

	private MultiValuedMap<WishKey, Wish> cache = new HashSetValuedHashMap<WishKey, Wish>();

	@PostConstruct
    public void init() {
		Boolean stopOnLoadError = env.<Boolean>getProperty("app.stop_on.load_error", Boolean.class, Boolean.FALSE);
		logger.trace("Stop on load error : {} ", stopOnLoadError);

		new ExcelFileReader(stopOnLoadError, resourceLoader, buildExcelFiles()).forEach(wishData -> add(wishData));
		logger.debug("Total Wishes {} ", cache.size());
		if (logger.isTraceEnabled()) {			
			for (WishKey key : cache.keySet()) {
				logger.trace("{} => {} ", key, cache.get(key).size());
			}
		}
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

		String fileNameKey = "app.name" + i + ".excel_file";
		String fileName = env.<String>getProperty(fileNameKey, String.class);

		if (!StringUtils.hasText(fileName)) {
			throw new ExcelFileReadError("Excel file name not specified, make sure to specify " + fileNameKey);
		}

		Integer nameIndex = env.<Integer>getProperty("app.name" + i + ".column", Integer.class);
		Integer emailIndex = env.<Integer>getProperty("app.email" + i + ".column", Integer.class);
		Integer dobIndex = env.<Integer>getProperty("app.dob" + i + ".column", Integer.class);
		Integer hireIndex = env.<Integer>getProperty("app.hire" + i + ".column", Integer.class);
		Integer sheetNumber = env.<Integer>getProperty("app.number" + i + ".excel_sheet", Integer.class, 1);

		ExcelFile file = new ExcelFile();
		file.setFileName(fileName);
		file.setNameIndex(nameIndex - 1);
		file.setEmailIndex(emailIndex - 1);
		if (dobIndex !=null) {			
			file.setDobIndex(dobIndex - 1);
		}
		if (hireIndex != null) {			
			file.setHireIndex(hireIndex - 1);
		}
		file.setSheetNumber(sheetNumber - 1);
		file.setWorkbookNumber(i);
		return file;
	}

	@Override
	public void add(WishData wishData) {
		List<Wish> wishes = buildWishes(wishData);
		wishes.forEach(wish -> cache.put(wish.getWishKey(), wish));		
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
		aWish.setWish("Happy Work Anniversary");
		aWish.setDetail("");
		return aWish;
	}

	private Wish buildBirthdayWish(WishData wishData) {
		Wish bWish = Wish.birthdayWish();
		bWish.setName(wishData.getName());
		bWish.setEmail(wishData.getEmail());
		bWish.setEventDate(wishData.getBirthDate());
		bWish.setPartition(wishData.getPartition());
		bWish.setWish("Happy Birthday");
		bWish.setDetail("");
		return bWish;
	}

	@Override
	public int forEach(LocalDate date, Consumer<Wish> wish) {
		WishKey wishKey = new WishKey(date.getMonthValue(), date.getDayOfMonth());
		Collection<Wish> wishes = cache.get(wishKey);
		wishes.forEach(wish);
		return wishes.size();
	}
}
