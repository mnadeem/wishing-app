package com.github.mnadeem.wishing.service;

import static com.github.mnadeem.wishing.Constants.PROPERTY_NAME_STOP_ON_LOAD_ERROR;
import static com.github.mnadeem.wishing.Constants.PROPERTY_NAME_WISH_FILES_COUNT;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

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
import com.github.mnadeem.wishing.service.support.data.WishData;
import com.github.mnadeem.wishing.service.support.data.WishFile;
import com.github.mnadeem.wishing.service.support.data.WishFiles;
import com.github.mnadeem.wishing.service.support.reader.WishFileReadError;
import com.github.mnadeem.wishing.service.support.reader.WishFilesReader;

/**
 * 
 * @author Mohammad Nadeem (coolmind182006@gmail.com)
 *
 */
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
		Boolean stopOnLoadError = env.<Boolean>getProperty(PROPERTY_NAME_STOP_ON_LOAD_ERROR, Boolean.class, Boolean.FALSE);
		logger.trace("Stop on load error : {} ", stopOnLoadError);

		new WishFilesReader(stopOnLoadError, resourceLoader, buildWishFiles()).forEach(wishData -> add(wishData));
		logger.debug("Total Wishes {} ", cache.size());
		if (logger.isTraceEnabled()) {			
			for (WishKey key : cache.keySet()) {
				logger.trace("{} => {} ", key, cache.get(key).size());
			}
		}
    }

	private WishFiles buildWishFiles() {
		Integer fileCount = env.<Integer>getProperty(PROPERTY_NAME_WISH_FILES_COUNT, Integer.class, 1);
		logger.debug("Loading {} wish files ", fileCount);
		List<WishFile> files = new ArrayList<>();
		for (int i = 0; i < fileCount; i++) {
			files.add(buildWishFile(i + 1));		
		}
		return new WishFiles(files);
	}

	private WishFile buildWishFile(int i) {

		String fileNameKey = "app.name" + i + ".wish_file";
		String fileName = env.<String>getProperty(fileNameKey, String.class);

		if (!StringUtils.hasText(fileName)) {
			throw new WishFileReadError("Wish file name not specified, make sure to specify " + fileNameKey);
		}

		Integer nameIndex = env.<Integer>getProperty("app.name" + i + ".column", Integer.class);
		Integer emailIndex = env.<Integer>getProperty("app.email" + i + ".column", Integer.class);
		Integer dobIndex = env.<Integer>getProperty("app.dob" + i + ".column", Integer.class);
		String dobFormat = env.<String>getProperty("app.dob" + i + ".date_format", String.class, "yyyy-MM-dd");
		Integer hireIndex = env.<Integer>getProperty("app.hire" + i + ".column", Integer.class);
		String hireFormat = env.<String>getProperty("app.hire" + i + ".date_format", String.class, "yyyy-MM-dd");
		Integer sheetNumber = env.<Integer>getProperty("app.number" + i + ".excel_sheet", Integer.class, 1);

		WishFile file = new WishFile();
		file.setFileName(fileName);
		file.setNameIndex(nameIndex - 1);
		file.setEmailIndex(emailIndex - 1);
		file.setDobFormat(dobFormat);
		file.setHireFormat(hireFormat);

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
	public int forEach(LocalDate date, BiConsumer<Wish, LocalDate> wish) {
		WishKey wishKey = new WishKey(date.getMonthValue(), date.getDayOfMonth());
		Collection<Wish> wishes = cache.get(wishKey);
		wishes.stream().forEach((c) -> wish.accept(c, date));
		return wishes.size();
	}
}
