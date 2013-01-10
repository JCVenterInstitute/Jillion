package org.jcvi.common.command.grid;

import org.jcvi.common.command.grid.JcviQueue;
import org.junit.Test;
import static org.junit.Assert.*;
public class TestJcviQueue {

	@Test
	public void getByName(){
		for(JcviQueue q : JcviQueue.values()){
			String name = q.getQueueName();
			assertSame(q, JcviQueue.getQueueFor(name));
		}
	}
}
