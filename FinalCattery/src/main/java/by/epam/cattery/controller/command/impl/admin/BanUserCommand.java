package by.epam.cattery.controller.command.impl.admin;

import by.epam.cattery.controller.command.ActionCommand;
import by.epam.cattery.controller.command.constant.PathConst;
import by.epam.cattery.controller.command.constant.SessionConst;

import by.epam.cattery.controller.content.NavigationType;
import by.epam.cattery.controller.content.RequestContent;
import by.epam.cattery.controller.content.RequestResult;

import by.epam.cattery.util.ConfigurationManager;

import by.epam.cattery.service.ServiceFactory;
import by.epam.cattery.service.UserService;
import by.epam.cattery.service.exception.ServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The command for baning the user.
 *
 */
public class BanUserCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(BanUserCommand.class);

    private static final String SUCCESS_PAGE = ConfigurationManager.getInstance().getProperty(PathConst.SUCCESS_PAGE);


    /**
     * Sets user's banned flag true and redirects to the success page.
     * All new reservations and awaiting offers of this user will be deleted.
     *
     * @param requestContent - {@link RequestContent) object that accumulates the data from request
     * @return {@link RequestResult) object that contains next page and the type of operation that will be performed
     * @throws ServiceException
     *
     */
    @Override
    public RequestResult execute(RequestContent requestContent) throws ServiceException {
        UserService userService = ServiceFactory.getInstance().getUserService();

        int userId = Integer.parseInt(requestContent.getParameter(SessionConst.ID));
        userService.banUser(userId);

        return new RequestResult(NavigationType.REDIRECT, SUCCESS_PAGE);
    }
}
