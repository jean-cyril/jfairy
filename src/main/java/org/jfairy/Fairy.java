/*
 * Copyright (c) 2013 Codearte
 */
package org.jfairy;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.jfairy.data.DataMaster;
import org.jfairy.producer.BaseProducer;
import org.jfairy.producer.DateProducer;
import org.jfairy.producer.company.Company;
import org.jfairy.producer.net.Network;
import org.jfairy.producer.payment.CreditCardProducer;
import org.jfairy.producer.person.Person;
import org.jfairy.producer.person.PersonFactory;
import org.jfairy.producer.person.PersonProperties;
import org.jfairy.producer.text.Text;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

/**
 * <p>An object that generates random personal data.</p>
 * <p/>
 * <p>Using a {@link #builder()}, you can configure the following fields:</p>
 * <ul>
 * <li><tt>locale</tt>: Specifies the locale for the random data file.</li>
 * <li><tt>filePrefix</tt>: Specifies the file prefix.
 * (So if you specify "jfairy" here and English for Locale, the data file will be
 * "jfairy_en.yml" under the classpath.)
 * </li>
 * <li><tt>random</tt>: The Random object to use.</li>
 * <li><tt>randomSeed</tt>: A specific random seed to use. Use this if you want the resulting
 * data to be <strong>deterministic</strong> based on it, such as if you want the same test
 * ID in a database to always result in the same fake name.
 * </li>
 * </ul>
 * <p/>
 * Obviously, don't set both <tt>random</tt> and <tt>randomSeed</tt>, only the last one you set will
 * actually take effect.
 */
public final class Fairy {

	private static final String DATA_FILE_PREFIX = "jfairy";

	private final Injector injector;

	private Fairy(Locale locale, String filePrefix, Random random) {
		injector = Guice.createInjector(new FairyModule(random));

		try {
			DataMaster dataMaster = injector.getInstance(DataMaster.class);
			dataMaster.readResources(filePrefix + ".yml");
			dataMaster.readResources(filePrefix + "_" + locale.getLanguage() + ".yml");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static class Builder {

		private Locale locale = Locale.ENGLISH;
		private String filePrefix = DATA_FILE_PREFIX;
		private Random random = new Random();

		private Builder() {
		}

		/**
		 * Sets the locale for the resulting Fairy.
		 *
		 * @param locale The Locale to set.
		 * @return the same Builder (for chaining).
		 */
		public Builder withLocale(Locale locale) {
			this.locale = locale;
			return this;
		}

		/**
		 * Sets the data file prefix for the resulting Fairy.
		 *
		 * @param filePrefix The prefix of the file (such as "jfairy" for "jfairy_en.yml").
		 * @return the same Builder (for chaining).
		 */
		public Builder withFilePrefix(String filePrefix) {
			this.filePrefix = filePrefix;
			return this;
		}

		/**
		 * Sets the Random object to use to pick things randomly.
		 *
		 * @param random The Random to use.
		 * @return the same Builder (for chaining).
		 */
		public Builder withRandom(Random random) {
			this.random = random;
			return this;
		}

		/**
		 * Sets the random seed to use to pick things randomly. (If you set this, you will always
		 * get the same result when you generate things.)
		 *
		 * @param randomSeed The random seed to use.
		 * @return the same Builder (for chaining).
		 */
		public Builder withRandomSeed(long randomSeed) {
			this.random = new Random(randomSeed);
			return this;
		}

		/**
		 * Returns the completed Fairy.
		 */
		public Fairy build() {
			return new Fairy(locale, filePrefix, random);
		}
	}

	/**
	 * Creates a Builder that will let you configure a Fairy's fields one by one.
	 *
	 * @return a Builder instance.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Use this factory method to create dataset containing default jfairy.yml and jfairy_{langCode}.yml files
	 * merged with custom files with the same name
	 *
	 * @return Fairy instance
	 */
	public static Fairy create() {
		return builder().build();
	}

	/**
	 * Use this factory method to create dataset containing default jfairy.yml and jfairy_{langCode}.yml files
	 * merged with custom files with the same name
	 *
	 * @param locale will be used to assess langCode for data file
	 * @return Fairy instance
	 */
	public static Fairy create(Locale locale) {
		return builder().withLocale(locale).build();
	}

	/**
	 * Use this factory method to create your own dataset overriding bundled one
	 *
	 * @param locale         will be used to assess langCode for data file
	 * @param dataFilePrefix prefix of the data file - final pattern will be jfairy.yml and dataFilePrefix_{langCode}.yml
	 * @return Fairy instance
	 */
	public static Fairy create(Locale locale, String dataFilePrefix) {
		return builder().withLocale(locale)
				.withFilePrefix(dataFilePrefix)
				.build();
	}

	/**
	 * Use this method for generating texts
	 *
	 * @return A {@link org.jfairy.producer.text.Text} instance
	 */
	public Text text() {
		return injector.getInstance(Text.class);
	}

	/**
	 * Use this method for fake persons
	 *
	 * @param personProperties desired person features
	 * @return A {@link org.jfairy.producer.person.Person} instance
	 */
	public Person person(PersonProperties.PersonProperty... personProperties) {
		PersonFactory personFactory = injector.getInstance(PersonFactory.class);
		return personFactory.producePerson(personProperties);
	}

	/**
	 * Use this method to generate fake company
	 *
	 * @return A {@link org.jfairy.producer.company.Company} instance
	 */
	public Company company() {
		return injector.getInstance(Company.class);
	}

	/**
	 * Use this method for get standard tools
	 *
	 * @return A {@link org.jfairy.producer.BaseProducer} instance
	 */
	public BaseProducer baseProducer() {
		return injector.getInstance(BaseProducer.class);
	}

	public DateProducer dateProducer() {
		return injector.getInstance(DateProducer.class);
	}

	public CreditCardProducer creditCard() {
		return injector.getInstance(CreditCardProducer.class);
	}

	public Network network() {
		return injector.getInstance(Network.class);
	}
}