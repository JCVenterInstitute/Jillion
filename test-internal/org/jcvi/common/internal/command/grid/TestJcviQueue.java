package org.jcvi.common.internal.command.grid;

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
