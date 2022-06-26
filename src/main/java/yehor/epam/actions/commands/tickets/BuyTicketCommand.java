package yehor.epam.actions.commands.tickets;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import yehor.epam.actions.BaseCommand;
import yehor.epam.exceptions.TicketException;
import yehor.epam.dao.SeatDAO;
import yehor.epam.dao.TicketDAO;
import yehor.epam.dao.factories.DAOFactory;
import yehor.epam.dao.factories.MySQLFactory;
import yehor.epam.entities.Ticket;
import yehor.epam.services.ErrorService;
import yehor.epam.utilities.RedirectManager;
import yehor.epam.utilities.LoggerManager;

import java.util.List;

import static yehor.epam.utilities.CommandConstants.COMMAND_VIEW_SUCCESS_PAY_PAGE;

public class BuyTicketCommand implements BaseCommand {
    private static final Logger logger = LoggerManager.getLogger(BuyTicketCommand.class);
    private String CLASS_NAME = BuyTicketCommand.class.getName();

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        try (DAOFactory factory = new MySQLFactory()) {
            logger.debug("Created DAOFactory in " + CLASS_NAME + " execute command");
            logger.debug("request.getAttribute(\"ticketList\") = " + request.getSession().getAttribute("ticketList").toString());
            List<Ticket> ticketList = (List<Ticket>) request.getSession().getAttribute("ticketList");

            final SeatDAO seatDao = factory.getSeatDao();
            final TicketDAO ticketDao = factory.getTicketDao();
            for (Ticket ticket : ticketList) {
                if (seatDao.isSeatFree(ticket.getSeat().getId(), ticket.getSession().getId())) {
                    logger.debug("isSeatFree. seatID: " + ticket.getSeat().getId() + "sessId: " + ticket.getSession().getId());
                    ticketDao.insert(ticket);
                } else {
                    logger.warn("Seat is already reserved");
                    throw new TicketException("Seat is already reserved, choose another one");
                }
            }
            response.sendRedirect(RedirectManager.getRedirectLocation(COMMAND_VIEW_SUCCESS_PAY_PAGE));
        } catch (Exception e) {
            ErrorService.handleException(request, response, CLASS_NAME, e);
        }
    }
}
