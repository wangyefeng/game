package org.game.spring.cache.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CfgActivityService {

    @Autowired
    private CfgActivityDao cfgActivityDao;

    @Cacheable({"activityList"})
    public List<CfgActivity> findAll() {
        return cfgActivityDao.findAll();
    }
}
