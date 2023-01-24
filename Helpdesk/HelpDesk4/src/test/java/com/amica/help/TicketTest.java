package com.amica.help;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;

import com.amica.help.Ticket.Priority;
import com.amica.help.Ticket.Status;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Unit test for the {@link Ticket} class.
 * 
 * @author Will Provost
 */
public class TicketTest {

  public static final String TECHNICIAN1_ID = "TECHNICIAN1_ID";
  public static final String TECHNICIAN1_NAME = "TECHNICIAN1_NAME";
  public static final int TECHNICIAN1_EXT = 12345;

  public static final String TECHNICIAN2_ID = "TECHNICIAN2_ID";
  public static final String TECHNICIAN2_NAME = "TECHNICIAN2_NAME";
  public static final int TECHNICIAN2_EXT = 56789;

  public static final int ID = 1;
  public static final String ORIGINATOR = "ORIGINATOR";
  public static final String DESCRIPTION = "DESCRIPTION";
  public static final Priority PRIORITY = Priority.HIGH;
  public static final String RESOLVE_REASON = "RESOLVE_REASON";
  public static final String WAIT_REASON = "WAIT_REASON";
  public static final String RESUME_REASON = "RESUME_REASON";
  public static final String NOTE = "NOTE";
  public static final Tag TAG1 = new Tag("TAG1");
  public static final Tag TAG2 = new Tag("TAG2");
      
  public static final String START_TIME = "1/3/22 13:37";
  
  protected Ticket ticket;
  
  /**
   * Custom matcher that assures that an {@link Event} added to a ticket
   * has the expected ticket ID, timestamp, status, and note. 
   */
  protected Matcher<Event> eventWith(Status status, String note) {
    return allOf(instanceOf(Event.class),
        hasProperty("ticketID", equalTo(ID)),
        hasProperty("timestamp", equalTo(Clock.getTime())),
        hasProperty("newStatus", equalTo(status)),
        hasProperty("note", equalTo(note)));
  }
  
  /**
   * Helper method to assert that the Nth (0-based) event on the target ticket
   * has the expected ID, timestamp, status, and note.
   */
  protected void assertHasEvent(int index, Status status, String note) {
    assertThat(ticket.getHistory().count(), equalTo(index + 1L));
    assertThat(ticket.getHistory().skip(index).findFirst().get(),
        eventWith(status, note));
  }

  private void passOneMinute(){
    Clock.setTime(Clock.getTime()+60000);
  }
  
  /**
   * Call init() to set the clock and create technicians
   * Create the test target.
   */
  @BeforeEach
  public void setUp() {
    Clock.setTime(START_TIME);
    ticket = new Ticket(ID, ORIGINATOR, DESCRIPTION, PRIORITY);
  }

  @Test
  public void testTicketCreation(){
    assertThat(ticket.getID(), equalTo(ID));
    assertThat(ticket.getOriginator(), equalTo(ORIGINATOR));
    assertThat(ticket.getDescription(), equalTo(DESCRIPTION));
    assertThat(ticket.getPriority(), equalTo(PRIORITY));
    assertThat(ticket.getStatus(), equalTo(Status.CREATED));
    assertThat(ticket.getTechnician(), equalTo(null));
    assertThat(ticket.getTags().count(), equalTo(0L));
    assertHasEvent(0, Status.CREATED, "Created ticket.");
  }

  @Test
  public void testTicketComparison(){
    assertThat(ticket, lessThan(new Ticket(2, ORIGINATOR, DESCRIPTION, Priority.LOW)));
    assertThat(ticket, greaterThan(new Ticket(3, ORIGINATOR, DESCRIPTION, Priority.URGENT)));
  }

//  @Test
//  public void testTicketAssignment(){
//    Clock.setTime("1/4/22 12:30");
//    ticket.assign(new Technician("a036830", "Brian", 12345));
//    assertThat(ticket.getStatus(), equalTo(Status.ASSIGNED));
//    assertThat(ticket.getTechnician().getName(), equalTo("Brian"));
//    assertHasEvent(1, Status.ASSIGNED, "Assigned to Technician a036830, Brian.");
//  }

  //Mocked version
  @Test
  public void testTicketAssignment(){
    Clock.setTime("1/4/22 12:30");
    Technician myMock = Mockito.mock(Technician.class);
    Mockito.when(myMock.getID()).thenReturn("a036830");
    Mockito.when(myMock.getName()).thenReturn("Brian");
    Mockito.when(myMock.toString()).thenReturn("Technician a036830, Brian");
    ticket.assign(myMock);

    assertThat(ticket.getStatus(), equalTo(Status.ASSIGNED));
    assertThat(ticket.getTechnician().getName(), equalTo("Brian"));
    assertHasEvent(1, Status.ASSIGNED, "Assigned to Technician a036830, Brian.");

    Mockito.verify(myMock).assignTicket(ticket);
  }


}
