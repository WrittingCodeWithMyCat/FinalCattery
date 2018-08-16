package by.epam.cattery.controller.command.impl;

import by.epam.cattery.controller.command.ActionCommand;
import by.epam.cattery.controller.command.constant.PathConst;
import by.epam.cattery.controller.command.constant.RequestConst;
import by.epam.cattery.controller.command.constant.SessionConst;
import by.epam.cattery.controller.command.util.DiscountHelper;
import by.epam.cattery.controller.content.NavigationType;
import by.epam.cattery.controller.content.RequestContent;
import by.epam.cattery.controller.content.RequestResult;
import by.epam.cattery.entity.LocaleLang;
import by.epam.cattery.entity.dto.CatDetail;
import by.epam.cattery.util.ConfigurationManager;
import by.epam.cattery.entity.Cat;
import by.epam.cattery.entity.Role;
import by.epam.cattery.service.CatService;
import by.epam.cattery.service.ServiceFactory;
import by.epam.cattery.service.UserService;
import by.epam.cattery.service.exception.ServiceException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class GoToSingleCatCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(GoToSingleCatCommand.class);

    @Override
    public RequestResult execute(RequestContent requestContent) throws ServiceException {
        CatService catService = ServiceFactory.getInstance().getCatService();
        UserService userService = ServiceFactory.getInstance().getUserService();

        String operation = requestContent.getParameter(RequestConst.OPERATION);
        int catId = Integer.parseInt(requestContent.getParameter(RequestConst.CAT_ID));
        int discountPercents = 0;
        LocaleLang localeLang = LocaleLang.valueOf(requestContent.getSessionAttribute(SessionConst.LOCALE).toString().toUpperCase());

        Cat cat = catService.takeSingleCat(catId, localeLang);

        CatDetail catDetailsRu = catService.getCatDetailsWithLocalization(catId, LocaleLang.RU);
        CatDetail catDetailsEn = catService.getCatDetailsWithLocalization(catId, LocaleLang.EN);

        if (requestContent.getSessionAttribute(SessionConst.ROLE) == Role.USER) {
            int userId = (int) requestContent.getSessionAttribute(SessionConst.ID);
            discountPercents = userService.getDiscount(userId);
            cat.setPriceWithDiscount(DiscountHelper.getInstance().countPriceWithDiscount(cat.getPrice(), discountPercents));

        } else {
            cat.setPriceWithDiscount(cat.getPrice());
        }

        requestContent.setAttribute(RequestConst.SINGLE_CAT, cat);
        requestContent.setAttribute(RequestConst.CATS_DETAILS_RU, catDetailsRu);
        requestContent.setAttribute(RequestConst.CATS_DETAILS_EN, catDetailsEn);

        return new RequestResult(NavigationType.FORWARD, ConfigurationManager.getInstance()
                .getProperty(PathConst.PATH_START + operation));
    }
}
