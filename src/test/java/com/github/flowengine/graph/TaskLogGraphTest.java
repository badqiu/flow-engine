package com.github.flowengine.graph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.mchange.v2.c3p0.DriverManagerDataSource;

public class TaskLogGraphTest {
	/*
	 * 
	 * 
tasklog_jdbc_url=jdbc:mysql://120.132.77.163:3306/tasklog
tasklog_jdbc_username=root
tasklog_jdbc_password=kingsoft0615
	 */
	@Test
	public void test(){
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClass("com.mysql.jdbc.Driver");
		ds.setPassword("kingsoft0615");
		ds.setUser("root");
		ds.setJdbcUrl("jdbc:mysql://120.132.77.163:3306/tasklog");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(ds);
		
		String sql = "select task_id,depends,exec_cost_time from tasklog where task_date='2015-08-31'";
		List<GraphNode> nodes = jdbcTemplate.query(sql, new RowMapper<GraphNode>() {
			public GraphNode mapRow(ResultSet rs, int rowNum) throws SQLException {
				GraphNode n = new GraphNode();
				n.setId(rs.getString("task_id"));
				n.setDepends(rs.getString("depends"));
				double costTime = rs.getInt("exec_cost_time") / 1000.0 / 60;
				n.setValue((int)costTime);
				return n;
			}
		});
		
		Graph graph = new Graph();
		graph.setNodes(nodes);
		graph.init();
		
		GraphTest.printGraph(graph);
	}
}
