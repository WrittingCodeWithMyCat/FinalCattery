package by.epam.cattery.controller.command.impl.expert;

import by.epam.cattery.controller.command.ActionCommand;
import by.epam.cattery.controller.command.constant.MessageConst;
import by.epam.cattery.controller.command.constant.PathConst;
import by.epam.cattery.controller.command.constant.RequestConst;
import by.epam.cattery.controller.command.constant.SessionConst;
import by.epam.cattery.controller.command.util.PathHelper;

import by.epam.cattery.controller.content.NavigationType;
import by.epam.cattery.controller.content.RequestContent;
import by.epam.cattery.controller.content.RequestResult;

import by.epam.cattery.entity.*;
import by.epam.cattery.entity.dto.CatDetail;
import by.epam.cattery.entity.dto.LocalizedCat;
import by.epam.cattery.service.exception.InvalidDateException;
import by.epam.cattery.util.ConfigurationManager;

import by.epam.cattery.service.CatService;
import by.epam.cattery.service.ServiceFactory;
import by.epam.cattery.service.exception.ServiceException;
import by.epam.cattery.service.exception.ValidationFailedException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class EditCatCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger(EditCatCommand.class);

    private static final String SUCCESS_PAGE = ConfigurationManager.getInstance().getProperty(PathConst.SUCCESS_PAGE);
    private static final String EDIT_CAT_COMMAND = ConfigurationManager.getInstance().getProperty(PathConst.EDIT_CAT_COMMAND);


    @Override
    public RequestResult execute(RequestContent requestContent) throws ServiceException {
        CatService catService = ServiceFactory.getInstance().getCatService();
        LocalizedCat cat = createLocalizedCat(requestContent);

        PathHelper pathHelper = PathHelper.getInstance();
        String path;

        String locale = requestContent.getSessionAttribute(SessionConst.LOCALE).toString();
        String message;

        try {
            catService.editCat(cat);

            path = SUCCESS_PAGE;

        } catch (ValidationFailedException e) {
            logger.log(Level.WARN, "Validation of input data failed during adding cat");
            message = ConfigurationManager.getInstance().getMessage(MessageConst.INVALID_INPUT, locale);
            path = pathHelper.addParameterToPath(EDIT_CAT_COMMAND, RequestConst.SENDING_CAT_FORM_FAILED_MESSAGE, message);
            path = pathHelper.addParameterToPath(path, RequestConst.CAT_ID, cat.getId());

        } catch (InvalidDateException e) {
            logger.log(Level.WARN, "Validation of input birthday failed during adding cat");
            message = ConfigurationManager.getInstance().getMessage(MessageConst.INVALID_BIRTH_DATE, locale);
            path = pathHelper.addParameterToPath(EDIT_CAT_COMMAND, RequestConst.SENDING_CAT_FORM_FAILED_MESSAGE, message);
            path = pathHelper.addParameterToPath(path, RequestConst.CAT_ID, cat.getId());
        }
        return new RequestResult(NavigationType.REDIRECT, path);
    }


    private LocalizedCat createLocalizedCat(RequestContent requestContent) {
        LocalizedCat cat = new LocalizedCat();

        cat.setId(Integer.parseInt(requestContent.getParameter(RequestConst.CAT_ID)));
        cat.setGender(Gender.valueOf(requestContent.getParameter(RequestConst.CAT_GENDER)));
        cat.setAge(requestContent.getParameter(RequestConst.CAT_AGE));
        cat.setPrice(Double.parseDouble(requestContent.getParameter(RequestConst.CAT_PRICE)));
        cat.setBodyColour(CatBodyColour.valueOf(requestContent.getParameter(RequestConst.CAT_BODY_COLOUR)));
        cat.setEyesColour(CatEyesColour.valueOf(requestContent.getParameter(RequestConst.CAT_EYES_COLOUR)));

        List<CatDetail> catDetailsWithLocalization = new ArrayList<>();

        CatDetail catDetailsRu = new CatDetail();
        catDetailsRu.setLocaleLang(LocaleLang.RU);
        catDetailsRu.setName(requestContent.getParameter(RequestConst.CAT_NAME_RU));
        catDetailsRu.setLastname(requestContent.getParameter(RequestConst.CAT_LASTNAME_RU));
        catDetailsRu.setDescription(requestContent.getParameter(RequestConst.CAT_DESCRIPTION_RU));
        catDetailsRu.setFemaleParent(requestContent.getParameter(RequestConst.CAT_FEMALE_PARENT_RU));
        catDetailsRu.setMaleParent(requestContent.getParameter(RequestConst.CAT_MALE_PARENT_RU));

        CatDetail catDetailsEn = new CatDetail();
        catDetailsEn.setLocaleLang(LocaleLang.EN);
        catDetailsEn.setName(requestContent.getParameter(RequestConst.CAT_NAME_EN));
        catDetailsEn.setLastname(requestContent.getParameter(RequestConst.CAT_LASTNAME_EN));
        catDetailsEn.setDescription(requestContent.getParameter(RequestConst.CAT_DESCRIPTION_EN));
        catDetailsEn.setFemaleParent(requestContent.getParameter(RequestConst.CAT_FEMALE_PARENT_EN));
        catDetailsEn.setMaleParent(requestContent.getParameter(RequestConst.CAT_MALE_PARENT_EN));

        catDetailsWithLocalization.add(catDetailsRu);
        catDetailsWithLocalization.add(catDetailsEn);

        cat.setCatDetailsWithLocalization(catDetailsWithLocalization);

        return cat;
    }
}
