package com.amica.help;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amica.HasKeys;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amica.help.Ticket.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the {@link HelpDesk} class.
 * 
 * @author Will Provost
 */
public class HelpDeskTest {

	public static final String TECH1 = "TECH1";
	public static final String TECH2 = "TECH2";
	public static final String TECH3 = "TECH3";

	public static final int TICKET1_ID = 1;
	public static final String TICKET1_ORIGINATOR = "TICKET1_ORIGINATOR";
	public static final String TICKET1_DESCRIPTION = "TICKET1_DESCRIPTION";
	public static final Priority TICKET1_PRIORITY = Priority.LOW;
	public static final int TICKET2_ID = 2;
	public static final String TICKET2_ORIGINATOR = "TICKET2_ORIGINATOR";
	public static final String TICKET2_DESCRIPTION = "TICKET2_DESCRIPTION";
	public static final Priority TICKET2_PRIORITY = Priority.HIGH;
	
	public static final String TAG1 = "TAG1";
	public static final String TAG2 = "TAG2";
	public static final String TAG3 = "TAG3";
	
	private HelpDesk helpDesk = new HelpDesk();
	private Technician tech1;
	private Technician tech2;
	private Technician tech3;

	public void createTicket1() {
		helpDesk.createTicket(TICKET1_ORIGINATOR, TICKET1_DESCRIPTION, TICKET1_PRIORITY);
	}

	public void createTicket2() {
		helpDesk.createTicket(TICKET2_ORIGINATOR, TICKET2_DESCRIPTION, TICKET2_PRIORITY);
	}

	/**
	 * Custom matcher that checks the contents of a stream of tickets
	 * against expected IDs, in exact order;
	 */
	public static class HasIDs extends TypeSafeMatcher<Stream<? extends Ticket>> {

		private String expected;
		private String was;
		
		public HasIDs(int... IDs) {
			int[] expectedIDs = IDs;
			expected = Arrays.stream(expectedIDs)
					.mapToObj(Integer::toString)
					.collect(Collectors.joining(", ", "[ ", " ]"));		
		}
		
		public void describeTo(Description description) {
			
			description.appendText("tickets with IDs ");
			description.appendText(expected);
		}
		
		@Override
		public void describeMismatchSafely
				(Stream<? extends Ticket> tickets, Description description) {
			description.appendText("was: tickets with IDs ");
			description.appendText(was);
		}

		protected boolean matchesSafely(Stream<? extends Ticket> tickets) {
			was = tickets.mapToInt(Ticket::getID)
					.mapToObj(Integer::toString)
					.collect(Collectors.joining(", ", "[ ", " ]"));
			return expected.equals(was);
		}
		
	}
	public static Matcher<Stream<? extends Ticket>> hasIDs(int... IDs) {
		return new HasIDs(IDs);
	}

	// Step5 uses a generic stream matcher:
//	public static Matcher<Stream<? extends Ticket>> hasIDs(Integer... IDs) {
//		return HasKeys.hasKeys(Ticket::getID, IDs);
//	}

	@BeforeEach
	public void setup(){
		helpDesk.addTechnician(TECH1, TECH1, 01);
		helpDesk.addTechnician(TECH2, TECH2, 02);
		helpDesk.addTechnician(TECH3, TECH3, 03);
		Clock.setTime(100);
		Iterator<Technician> iterator = helpDesk.getTechnicians().iterator();
		tech1 = iterator.next();
		tech2 = iterator.next();
		tech3 = iterator.next();
	}

	@Test
	public void testNoTechnicians(){
		HelpDesk tmp = new HelpDesk();
		assertThrows(IllegalStateException.class, () -> tmp.createTicket(TICKET1_ORIGINATOR, TICKET1_DESCRIPTION, TICKET1_PRIORITY));
	}

	@Test
	public void testTicketDescription(){
		createTicket1();
		createTicket2();
		assertThat(helpDesk.getTicketByID(TICKET2_ID), hasProperty("description", equalTo(TICKET2_DESCRIPTION)));
	}

	@Test
	public void testNoTickets(){
		System.out.println(helpDesk.getTickets());
		assertThat((int) helpDesk.getTickets().count(), equalTo(0));
	}

	@Test
	public void testTicketAssignment(){
		createTicket1();
		assertThat(helpDesk.getTicketByID(1).getTechnician(), equalTo(tech1));
	}

	@Test
	public void testTicketAssignment2(){
		createTicket1();
		tech2.assignTicket(helpDesk.getTicketByID(1));
		createTicket2();
		assertThat(helpDesk.getTicketByID(2).getTechnician(), equalTo(tech3));
	}

	@Test
	public void testQueryByStatus(){
		createTicket1();
		createTicket2();
		helpDesk.getTicketByID(2).resolve("Test resolution");
		assertThat((int)helpDesk.getTicketsByStatus(Ticket.Status.ASSIGNED).count(), equalTo(1));
		assertThat((int)helpDesk.getTicketsByStatus(Ticket.Status.RESOLVED).count(), equalTo(1));
	}

	@Test
	public void testQueryByNotStatus(){
		createTicket1();
		createTicket2();
		assertThat(helpDesk.getTicketsByNotStatus(Ticket.Status.WAITING), hasIDs(2, 1));
	}

	@Test
	public void testQueryByTag(){
		createTicket1();
		createTicket2();
		helpDesk.getTicketByID(1).addTag(new Tag("VPN"));
		helpDesk.getTicketByID(2).addTag(new Tag("Connection"));
		assertThat(helpDesk.getTicketsWithAnyTag("VPN"), hasIDs(1));
	}

	@Test
	public void testQueryByTechnician(){
		createTicket1();
		tech1.assignTicket(helpDesk.getTicketByID(1));
		assertThat(helpDesk.getTicketsByTechnician(TECH1), hasIDs(1));
	}

	@Test
	public void testQueryByText(){
		createTicket1();
		assertThat(helpDesk.getTicketsByText(TICKET1_DESCRIPTION), hasIDs(1));
	}
}

