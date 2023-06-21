package com.nowcoder.community.dao;

import org.springframework.stereotype.Repository;

@Repository
public class AplhaDaoHibernateImpl implements AlphaDao{
    @Override
    public String select() {
        return "hhah";
    }
}
