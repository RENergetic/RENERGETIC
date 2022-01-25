package com.renergetic.backdb.service;

import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.renergetic.backdb.model.Asset;
import com.renergetic.backdb.model.User;
import com.renergetic.backdb.repository.AssetRepository;
import com.renergetic.backdb.repository.ConnectionRepository;
import com.renergetic.backdb.repository.InfrastructureRepository;
import com.renergetic.backdb.repository.UserRepository;

@Service
public class UserService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	UserRepository userRepository;
	@Autowired
	AssetRepository assetRepository;
	@Autowired
	InfrastructureRepository infrastructureRepository;
	@Autowired
	ConnectionRepository connecRepository;
	
	public Asset getUserResidence(long userId) {
		User user = userRepository.getById(userId);
		return assetRepository.getById(user.getResideAssetId());
	}
	
	@Transactional
	@Modifying
	public User update(User user, long id) {
		ArrayList<String> setClause = new ArrayList<>();
		
		if (!user.getName().isEmpty())
			setClause.add("name=:name");

		if (user.getResideAssetId() != 0)
			setClause.add("reside_asset_id=:asset");
		
		if (setClause.size() > 0) {
			Session session = entityManager.unwrap(Session.class);
			
			Query query = session.createNativeQuery(
					String.format("UPDATE people SET %sWHERE id=:id", String.join(", ", setClause)))
					.setParameter("id", id);
			
			if (!user.getName().isEmpty())
				query.setParameter("name", user.getName());
	
			if (user.getResideAssetId() != 0)
				query.setParameter("asset", user.getResideAssetId());
			
			query.executeUpdate();
			session.close();
		}
		return user;
	}
}
