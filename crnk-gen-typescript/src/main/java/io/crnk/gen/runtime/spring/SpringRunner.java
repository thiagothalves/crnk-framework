package io.crnk.gen.runtime.spring;

import java.io.IOException;

import io.crnk.gen.runtime.GeneratorTrigger;
import io.crnk.meta.MetaLookup;
import io.crnk.meta.MetaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringRunner {

	public void run(GeneratorTrigger context) {
		SpringRuntimeConfig springConfig = context.getConfig().getRuntime().getSpring();
		if (springConfig.getConfiguration() == null) {
			throw new IllegalStateException("typescriptGen.runtime.spring.configuration not specified");
		}
		if (springConfig.getProfile() == null) {
			throw new IllegalStateException("typescriptGen.runtime.spring.profile not specified");
		}
		ConfigurableApplicationContext applicationContext = null;
		try {
			Class configurationClass = getClass().getClassLoader().loadClass(springConfig.getConfiguration());

			SpringApplication application = new SpringApplication(configurationClass);
			application.setWebEnvironment(false);
			application.setAdditionalProfiles(springConfig.getProfile());
			applicationContext = application.run();

			MetaModule metaModule = applicationContext.getBean(MetaModule.class);
			MetaLookup lookup = metaModule.getLookup();
			context.generate(lookup);
		}
		catch (ClassNotFoundException e) {
			throw new IllegalStateException(e);
		}
		catch (IOException e) {
			throw new IllegalStateException(e);
		}
		finally {
			if (applicationContext != null) {
				applicationContext.close();
			}
		}
	}
}
