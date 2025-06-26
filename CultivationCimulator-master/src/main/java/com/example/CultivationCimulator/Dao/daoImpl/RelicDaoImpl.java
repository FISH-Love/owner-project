package com.example.CultivationCimulator.Dao.daoImpl;

import com.example.CultivationCimulator.Dao.RelicDao;
import com.example.CultivationCimulator.POJO.Relic;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Repository
@Transactional
public class RelicDaoImpl implements RelicDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Relic> findByRelicType(int relicType) {
        String jpql = "SELECT r FROM Relic r WHERE r.relicType = :type";
        return entityManager.createQuery(jpql, Relic.class)
                .setParameter("type", relicType)
                .getResultList();
    }

    @Override
    public Relic findRandomByType(int relicType) {
        List<Relic> relics = findByRelicType(relicType);
        if (relics.isEmpty()) {
            return null;
        }
        // 随机获取一个遗物
        return relics.get(new Random().nextInt(relics.size()));
    }
    @Override
    public Optional<Relic> findById(Long id) {
        Relic relic = entityManager.find(Relic.class, id);
        return Optional.ofNullable(relic);
    }

    @Override
    public Relic save(Relic relic) {
        if (relic.getId() == null) {
            // 新增
            entityManager.persist(relic);
            return relic;
        } else {
            // 更新
            return entityManager.merge(relic);
        }
    }

    @Override
    public void deleteById(Long id) {
        Relic relic = entityManager.find(Relic.class, id);
        if (relic != null) {
            entityManager.remove(relic);
        }
    }
}