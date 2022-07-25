package hr.algebra.repository;

import hr.algebra.sql.SqlRepo;

public class RepoFactory {

    private RepoFactory() {}

    public static ISqlRepo getRepository() throws Exception {
        return new SqlRepo();
    }
}
