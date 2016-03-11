/**   
* 文件名：HibernateDao.java   
*   
* 版本信息：   
* 日期：2014-1-7   
* Copyright  
* 版权所有   
*   
*/
package zjoy.web.common.dao.hibernate;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.transform.Transformers;
import org.springframework.transaction.annotation.Transactional;

import zjoy.web.common.dao.Page;




public class HibernateDao<T,PrimaryKey extends Serializable> {
	/** spring 注入hibernate sessionFactory*/
	
	private SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public T get(Class<T> entityClass,PrimaryKey id) {
		return (T) sessionFactory.getCurrentSession().get(entityClass, id);
	}

	@Transactional
	public void save(T entity) {
		sessionFactory.getCurrentSession().save(entity);
	}

	@Transactional
	public void update(T entity) {
		sessionFactory.getCurrentSession().update(entity);
		
	}

	@Transactional
	public void delete(T entity) {
		sessionFactory.getCurrentSession().delete(entity);
	}
	@Transactional
	public void delete(Class<T> entityClass,PrimaryKey id){
		delete(get(entityClass,id));
	}

	@Transactional
	public void merge(T entity) {
		sessionFactory.getCurrentSession().merge(entity);		
	}

	@Transactional
	public void saveOrUpdate(T entity) {
		sessionFactory.getCurrentSession().saveOrUpdate(entity);		
	}

	@SuppressWarnings("unchecked")
	public T findUniqueByCriteria(Class<T> entityClass,final Criterion... criterion){
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(entityClass);
		for(Criterion c:criterion){
			criteria.add(c);
		}
		criteria.setProjection(null);
		// 限定了返回结果的样式为只是查询主体,不带关联查询的对象 
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return (T) criteria.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public void findPageByDetachedCriteriaProjection(
			DetachedCriteria detachedCriteria, Page<T> page) {
		
		Criteria criteria = detachedCriteria.getExecutableCriteria(sessionFactory.getCurrentSession());
		int total = criteria.list().size();    //总记录数
		long pageNo = page.getPage();
		long pageSize = page.getRows();

		List<T> items = criteria.setFirstResult((int) ((pageNo -1)*pageSize))
				.setMaxResults((int) pageSize).list();
		page.setList(items);
		page.setTotal(total);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> findByCriteriaSpecification(
			CriteriaSpecification criteriaSpecification) {
		List<Object[]> list = new ArrayList<Object[]>();
		Criteria criteria = null;
		if(criteriaSpecification instanceof Criteria){
			criteria = (Criteria) criteriaSpecification;
		}else if(criteriaSpecification instanceof DetachedCriteria){
			criteria = ((DetachedCriteria)criteriaSpecification).getExecutableCriteria(sessionFactory.getCurrentSession());
		}else{
			return list;
		}
		return criteria.list();
	}

	/** 根据0~n个查询条件 获取所有记录 */
	@SuppressWarnings("unchecked")
	public List<T> findAllByCriteria(Class<T> entityClass,final Criterion... criterion) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(entityClass);
		for(Criterion c:criterion){
			criteria.add(c);
		}
		criteria.setProjection(null);
		// 限定了返回结果的样式为只是查询主体,不带关联查询的对象 
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findAllByCriteriaSpecification(
			CriteriaSpecification criteriaSpecifications) {
		List<T> list = new ArrayList<T>();
		Criteria criteria = null;
		if(criteriaSpecifications instanceof Criteria){
			criteria = (Criteria) criteriaSpecifications;
		}else if(criteriaSpecifications instanceof DetachedCriteria){
			criteria = ((DetachedCriteria)criteriaSpecifications).getExecutableCriteria(sessionFactory.getCurrentSession());
		}else{
			return list;
		}
		criteria.setProjection(null);
		// 限定了返回结果的样式为只是查询主体,不带关联查询的对象 
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria.list();
	}

	
	@SuppressWarnings("unchecked")
	public void findPageByDetachedCriteria(
			DetachedCriteria detachedCriteria, Page<T> page) {
		Criteria criteria = null;
		String order = page.getOrders();
		String sort = page.getSorts();
		
		if(StringUtils.isNotEmpty(order) && StringUtils.isNotEmpty(sort)){
			String[] orders = order.split(" ");
			String[] sorts = sort.split(" ");
			for(int i = 0;i<orders.length;i++){			
				if(StringUtils.equalsIgnoreCase("asc", orders[i])&&StringUtils.isNotEmpty(sorts[i])){
					detachedCriteria.addOrder(Order.asc(sorts[i]));
				}else if(StringUtils.equalsIgnoreCase("desc", orders[i])&&StringUtils.isNotEmpty(sorts[i])){
					detachedCriteria.addOrder(Order.desc(sorts[i]));
				}
			}
		}
		
		
		criteria = detachedCriteria.getExecutableCriteria(sessionFactory.getCurrentSession());
		
		long total = this.getCountBycriteriaSpecification(criteria);
		criteria.setProjection(null);
		// 限定了返回结果的样式为只是查询主体,不带关联查询的对象 
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);		
		
		long pageNo = page.getPage();
		long pageSize = page.getRows();
		List<T> items = criteria.setFirstResult((int) ((pageNo -1)*pageSize))
				.setMaxResults((int) pageSize).list();
		page.setList(items);
		page.setTotal(total);
		
	}

	

	/** 获取符合查询条件的记录数 */
	public long getCountByCriteria(Class<T> entityClass,final Criterion... criterion) {
		Criteria criteria = sessionFactory.getCurrentSession()
				.createCriteria(entityClass);
		if (criterion != null) {
			for (Criterion c : criterion) {
				criteria.add(c);
			}
		}
		return getCountBycriteriaSpecification(criteria);
	}
	
	/** 取得符合条件的记录数 */
	public long getCountBycriteriaSpecification(final CriteriaSpecification criteriaSpecification){
		Criteria criteria = null;
		if(criteriaSpecification instanceof Criteria){
			criteria = (Criteria) criteriaSpecification;
		}else if(criteriaSpecification instanceof DetachedCriteria){
			criteria = ((DetachedCriteria)criteriaSpecification).getExecutableCriteria(sessionFactory.getCurrentSession());
		}else{
			return 0;
		}
		String countStr = criteria.setProjection(
				Projections.rowCount()).uniqueResult()+"";
		long count = Long.valueOf(countStr.equals("null")?"0":countStr);
		return count;
	}
	
	@Transactional
	public void updateByHql(String hql, Object...params) {
		 Query query = sessionFactory.getCurrentSession().createQuery(hql);
         for(int i=0; i<params.length; i++){
             query.setParameter(i, params[i]);
         }
         query.executeUpdate();
	}
	
	
	@Transactional
	public void updateBySql(String sql) {
		 Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
         query.executeUpdate();
	}
	
	public void updateByHqlForHibernate4(String hql, Map<String, Object> params) {
		
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		for(Map.Entry<String, Object> entry : params.entrySet()){
			query.setParameter(entry.getKey(), entry.getValue());
		}
        query.executeUpdate();
	}

	
	@SuppressWarnings("unchecked")
	public T findUniqueByHql(String hql) {
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
        T t = (T)query.uniqueResult();
        return t;
	}
	
	
	@SuppressWarnings("unchecked")
	public Object findUniqueBySql(String sql) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        Object o = query.uniqueResult();
        return o;
	}

	
	@SuppressWarnings("unchecked")
	public List<T> findByHql(String hql, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		for(Map.Entry<String, Object> entry : params.entrySet()){
			query.setParameter(entry.getKey(), entry.getValue());
		}
        return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByHqlLimit(String hql, Integer index, Integer num, Map<String, Object> params) {
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		for(Map.Entry<String, Object> entry : params.entrySet()){
			query.setParameter(entry.getKey(), entry.getValue());
		}
        query.setFirstResult(index);
        query.setMaxResults(num);
        return query.list();
	}

	
	@SuppressWarnings("unchecked")
	public void findPageByHql(Page<T> page,String hql,String cols, Object ... params) {
		Query queryCount = null;
		if(hql.contains("select")){
			queryCount = sessionFactory.getCurrentSession().createQuery("select count(d) from ("+hql+") d");
		}else{
			queryCount = sessionFactory.getCurrentSession().createQuery("select count(*) "+hql);
		}
        for(int position = 0; position < params.length; position ++){
        	queryCount.setParameter(position, params[position]);
        }
        long total =  (Long) queryCount.uniqueResult();     //总记录数
        
        if(!org.springframework.util.StringUtils.isEmpty(cols)){
        	hql="select "+cols+" "+hql;
        }
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        for(int position = 0; position < params.length; position ++){
        	query.setParameter(position, params[position]);
        }
        long pageNo = page.getPage();
		int pageSize = page.getRows();
        //用于分页查询
        if(pageSize > 0){
            query.setFirstResult(new Long((pageNo -1)*pageSize).intValue());
            query.setMaxResults(pageSize);
        }
        List<T> list = query.list();
        page.setList(list);
        page.setTotal(total);
		
	}
	
	@SuppressWarnings("unchecked")
	public void findPageByHql(Page<T> page,String hql,String totalSql) {
		Query queryCount = sessionFactory.getCurrentSession().createQuery(totalSql);
		long total =  (Long) queryCount.uniqueResult();     //总记录数
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        long pageNo = page.getPage();
		int pageSize = page.getRows();
        //用于分页查询
        if(pageSize > 0){
            query.setFirstResult(new Long((pageNo -1)*pageSize).intValue());
            query.setMaxResults(pageSize);
        }
        List<T> list = query.list();
        page.setList(list);
        page.setTotal(total);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void findPageByHql(Page<T> page,String hql,String totalSql,long total) {
		if(total==0){
			Query queryCount = sessionFactory.getCurrentSession().createQuery(totalSql);
			total =  (Long) queryCount.uniqueResult();     //总记录数
		}
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        long pageNo = page.getPage();
		int pageSize = page.getRows();
        //用于分页查询
        if(pageSize > 0){
            query.setFirstResult(new Long((pageNo -1)*pageSize).intValue());
            query.setMaxResults(pageSize);
        }
        List<T> list = query.list();
        page.setList(list);
        page.setTotal(total);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void findPageBySql(Page<T> page,String sql) {
        Query queryCount = sessionFactory.getCurrentSession().createSQLQuery("select count(*) from ("+sql+") b");
       BigInteger integer =   (BigInteger) queryCount.uniqueResult();     //总记录数
        
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        long pageNo = page.getPage();
		int pageSize = page.getRows();
        //用于分页查询
        if(pageSize > 0){
            query.setFirstResult(new Long((pageNo -1)*pageSize).intValue());
            query.setMaxResults(pageSize);
        }
        List<T> list = query.list();
        page.setList(list);
        page.setTotal(integer.longValue());
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void findPageBySql(Page<T> page,String sql,String totalSql) {
        Query queryCount = sessionFactory.getCurrentSession().createSQLQuery(totalSql);
       BigInteger integer =   (BigInteger) queryCount.uniqueResult();     //总记录数
        
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        long pageNo = page.getPage();
		int pageSize = page.getRows();
        //用于分页查询
        if(pageSize > 0){
            query.setFirstResult(new Long((pageNo -1)*pageSize).intValue());
            query.setMaxResults(pageSize);
        }
        List<T> list = query.list();
        page.setList(list);
        page.setTotal(integer.longValue());
		
	}
	
	/**
	 * 
	 * @author zhouyang
	 * @createDate 2015年11月27日
	 * @description 如果传进来total 则不再查询总数
	 */
	@SuppressWarnings("unchecked")
	public void findPageBySql(Page<T> page,String sql,String totalSql,int total) {
        Query queryCount = sessionFactory.getCurrentSession().createSQLQuery(totalSql);
        if(total==0){
        	BigInteger integer =   (BigInteger) queryCount.uniqueResult();     //总记录数
        	total = integer.intValue();
        }
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        long pageNo = page.getPage();
		int pageSize = page.getRows();
        //用于分页查询
        if(pageSize > 0){
            query.setFirstResult(new Long((pageNo -1)*pageSize).intValue());
            query.setMaxResults(pageSize);
        }
        List<T> list = query.list();
        page.setList(list);
        page.setTotal(total);
		
	}
	
	@SuppressWarnings("unchecked")
	public List findAllBySql(String sql) {
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        //用于分页查询
        List list = query.list();
        return list;
	}
	
	public Page findAllBySql(String sql,Page page) {
		String countQuery = "select count(1) from (" +sql+" ) cou";
		Query queryCount = sessionFactory.getCurrentSession().createSQLQuery(countQuery);
		BigInteger integer =   (BigInteger) queryCount.uniqueResult();     //总记录数
    	long total = integer.intValue();
        Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
        query.setFirstResult(new Long((page.getPage() -1)*page.getRows()).intValue());
        query.setMaxResults(page.getRows());
        //用于分页查询
        List list = query.list();
        page.setList(list);
        page.setTotal(total);
        return page;
	}
	

	
	public long getCountByHql(String hql, Object ... params) {
		 Query query = sessionFactory.getCurrentSession().createQuery(hql);
	     return (Long) query.iterate().next();
	}
	
	public long getCountBySql(String sql) {
		Query queryCount = sessionFactory.getCurrentSession().createSQLQuery(sql);
		BigInteger integer =   (BigInteger) queryCount.uniqueResult();     //总记录数
		return integer.longValue();
	}

	
	public Session getHibernateSession() {
		return sessionFactory.getCurrentSession();
	}
	
	 
	/**
	 * 根据条件查询，适用于只查询部分字段的情况
	 * @param detachedCriteria
	 * @param reqParam
	 * @param ignoreProperty，忽略的字段，使用分隔符隔开，如：userId,mobile,img
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public void findPageByDetachedCriteria(DetachedCriteria detachedCriteria, Page<T> page,String ignoreProperty) throws ClassNotFoundException{
		Criteria criteria = null;
		String order = page.getOrders();
		String sort = page.getSorts();
		
		if(StringUtils.isNotEmpty(order) && StringUtils.isNotEmpty(sort)){
			String[] orders = order.split(" ");
			String[] sorts = sort.split(" ");
			for(int i = 0;i<orders.length;i++){			
				if(StringUtils.equalsIgnoreCase("asc", orders[i])&&StringUtils.isNotEmpty(sorts[i])){
					detachedCriteria.addOrder(Order.asc(sorts[i]));
				}else if(StringUtils.equalsIgnoreCase("desc", orders[i])&&StringUtils.isNotEmpty(sorts[i])){
					detachedCriteria.addOrder(Order.desc(sorts[i]));
				}
			}
		}
		
		criteria = detachedCriteria.getExecutableCriteria(sessionFactory.getCurrentSession());
		
		long total = this.getCountBycriteriaSpecification(criteria);
		criteria.setProjection(null);
		
		Map<String,String> propertyMap=new HashMap<String,String>();
		String propertys[]=ignoreProperty.split(",");
		for(String property:propertys){
			propertyMap.put(property, property);
		}
		Class c=Class.forName((((CriteriaImpl) criteria).getEntityOrClassName()));
		Field[] fields=c.getDeclaredFields();
		
		ProjectionList pList = Projections.projectionList(); 
		for(Field field:fields){
			boolean transientFlag=false;
			for(Annotation annotation:field.getAnnotations()){
//				System.out.println(annotation.annotationType());
				if(annotation.annotationType().equals(javax.persistence.Transient.class)
						||annotation.annotationType().equals(javax.persistence.OneToOne.class)){
					transientFlag=true;
					break;
				}
			}
			if(transientFlag){
				continue;
			}
			if(propertyMap.get(field.getName())==null){
				pList.add(Projections.property(field.getName()).as(field.getName()));
			}
		}
		criteria.setProjection(pList);
//		criteria.setProjection(Projections.distinct(pList));
		criteria.setResultTransformer(Transformers.aliasToBean(c));
		
		// 限定了返回结果的样式为只是查询主体,不带关联查询的对象 
				
		
		long pageNo = page.getPage();
		long pageSize = page.getRows();
		List items = criteria.setFirstResult((int) ((pageNo -1)*pageSize))
				.setMaxResults((int) pageSize).list();
		
		page.setList(items);
		page.setTotal(total);
	}

	
	@SuppressWarnings("unchecked")
	public T findUniqueByDetachedCriteria(DetachedCriteria detachedCriteria){
		Criteria criteria = detachedCriteria.getExecutableCriteria(sessionFactory.getCurrentSession());
		detachedCriteria.setProjection(null);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return (T) criteria.uniqueResult();
	}
	
	public T findLastestOneByDetachedCriteria(DetachedCriteria  detachedCriteria){
		Criteria criteria = detachedCriteria.getExecutableCriteria(sessionFactory.getCurrentSession());
		detachedCriteria.setProjection(null);
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		List<T> list = criteria.list();
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
}
