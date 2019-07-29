package com.harystolho.tdb_server.cluster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.harystolho.tdb_server.cluster.command.InsertItemCommand;
import com.harystolho.tdb_server.cluster.command.ReadItemCommand;
import com.harystolho.tdb_server.cluster.query.ItemFieldQuery;
import com.harystolho.tdb_server.transaction.CommandLogger;
import com.harystolho.tdb_shared.QueryResult;

@ExtendWith(MockitoExtension.class)
public class ClusterTest {

	private Cluster cluster;

	private List<Item> items;

	@Mock
	private CommandLogger logger;

	@SuppressWarnings("unchecked")
	@BeforeEach
	public void beforeEach() {
		items = Mockito.mock(List.class);
		cluster = new Cluster("TEST_CLUSTER", items, logger);
	}

	@Test
	public void insertItemCommand_ShouldInsertItem() {
		Mockito.when(items.add(Mockito.any(Item.class))).then((inv) -> {
			Item item = (Item) inv.getArgument(0);

			assertEquals("john", item.get("name"));
			assertEquals("7", item.get("age"));

			return true;
		});

		InsertItemCommand iic = new InsertItemCommand(4, "TEST_CLUSTER", Map.of("name", "john", "age", "7"));

		cluster.handle(iic);
	}

	@Test
	public void readItemCommand_SingleQuery_ShouldReturnItemsThatMatch() {
		Cluster cluster = new Cluster("CLOTHES", new ArrayList<>(), logger);

		cluster.handle(new InsertItemCommand(7, "CLOTHES", Map.of("color", "brown", "size", "M")));
		cluster.handle(new InsertItemCommand(7, "CLOTHES", Map.of("color", "yellow", "size", "L")));
		cluster.handle(new InsertItemCommand(7, "CLOTHES", Map.of("color", "red", "size", "S")));
		cluster.handle(new InsertItemCommand(7, "CLOTHES", Map.of("color", "black", "size", "S")));

		ReadItemCommand ric = new ReadItemCommand("CLOTHES", ItemFieldQuery.equal("size", "M"));

		QueryResult result = cluster.handle(ric);

		List<Map> list = result.getList("items", Map.class);

		assertEquals("brown", list.get(0).get("color"));
	}

	@Test
	public void readItemCommand_CompositeQuery_ShouldReturnItemsThatMatch() {
		Cluster cluster = new Cluster("VEHICLES", new ArrayList<>(), logger);

		cluster.handle(new InsertItemCommand(14, "VEHICLES", Map.of("year", "2011", "color", "green", "brand", "JD")));
		cluster.handle(
				new InsertItemCommand(14, "VEHICLES", Map.of("year", "2017", "color", "yellow", "brand", "CAT")));
		cluster.handle(new InsertItemCommand(14, "VEHICLES", Map.of("year", "2016", "color", "red", "brand", "MF")));
		cluster.handle(
				new InsertItemCommand(14, "VEHICLES", Map.of("year", "2019", "color", "yellow", "brand", "CAT")));

		ReadItemCommand ric = new ReadItemCommand("VEHICLES",
				ItemFieldQuery.equal("year", "2017").and(ItemFieldQuery.equal("brand", "CAT")));

		QueryResult result = cluster.handle(ric);

		List<Map> list = result.getList("items", Map.class);

		assertEquals("yellow", list.get(0).get("color"));
	}

	@Test
	public void readItemCommand_ShouldReturnEmptyIfNoItemsMatch() {
		Cluster cluster = new Cluster("VEHICLES", new ArrayList<>(), logger);

		cluster.handle(new InsertItemCommand(14, "VEHICLES", Map.of("year", "2011", "color", "green", "brand", "JD")));
		cluster.handle(
				new InsertItemCommand(14, "VEHICLES", Map.of("year", "2017", "color", "yellow", "brand", "CAT")));
		cluster.handle(new InsertItemCommand(14, "VEHICLES", Map.of("year", "2016", "color", "red", "brand", "MF")));
		cluster.handle(
				new InsertItemCommand(14, "VEHICLES", Map.of("year", "2019", "color", "yellow", "brand", "CAT")));

		ReadItemCommand ric = new ReadItemCommand("VEHICLES",
				ItemFieldQuery.equal("year", "2019").and(ItemFieldQuery.equal("brand", "Jd")));

		QueryResult result = cluster.handle(ric);

		List<Map> list = result.getList("items", Map.class);

		assertTrue(list.isEmpty());
	}

}
