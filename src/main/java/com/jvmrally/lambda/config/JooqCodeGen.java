package com.jvmrally.lambda.config;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;

/**
 * JooqCodeGen
 */
public class JooqCodeGen {

    private JooqCodeGen() {
    }

    public static void runJooqCodeGen(String url, String user, String password) throws Exception {
        var driver = System.getenv("LAMBDA_DB_DRIVER");
        Configuration configuration =
                new Configuration()
                        .withJdbc(new Jdbc().withDriver(driver).withUrl(url).withUser(user)
                                .withPassword(password))
                        .withGenerator(new Generator()
                                .withDatabase(new Database()
                                        .withExcludes(
                                                "flyway_schema_history|information_schema.*|pg_.*")
                                        .withInputSchema("public")
                                        .withOutputSchemaToDefault(Boolean.TRUE))
                                .withGenerate(new Generate().withPojos(Boolean.TRUE)
                                        .withDeprecationOnUnknownTypes(Boolean.FALSE))
                                .withTarget(new Target().withPackageName("com.jvmrally.lambda.db")
                                        .withDirectory("src/main/java")));

        GenerationTool.generate(configuration);
    }
}
