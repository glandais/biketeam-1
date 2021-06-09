package info.tomacla.biketeam.web.admin.ride;

import info.tomacla.biketeam.common.PublishedStatus;
import info.tomacla.biketeam.domain.ride.Ride;
import info.tomacla.biketeam.domain.template.RideTemplate;
import info.tomacla.biketeam.service.MapService;
import info.tomacla.biketeam.service.RideService;
import info.tomacla.biketeam.service.RideTemplateService;
import info.tomacla.biketeam.web.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/admin/rides")
public class AdminRideController extends AbstractController {

    @Autowired
    private RideService rideService;

    @Autowired
    private MapService mapService;

    @Autowired
    private RideTemplateService rideTemplateService;


    @GetMapping
    public String getRides(Principal principal, Model model) {
        addGlobalValues(principal, model, "Administration - Rides");
        model.addAttribute("rides", rideService.listRides());
        model.addAttribute("templates", rideTemplateService.listTemplates());
        return "admin_rides";
    }

    @GetMapping(value = "/new")
    public String newRide(@RequestParam(value = "templateId", required = false, defaultValue = "empty-1") String templateId,
                          Principal principal,
                          Model model) {

        NewRideForm form = null;

        if (templateId != null && !templateId.startsWith("empty-")) {
            Optional<RideTemplate> optionalTemplate = rideTemplateService.get(templateId);
            if (optionalTemplate.isPresent()) {
                form = NewRideForm.builder(optionalTemplate.get()).get();
            }
        }

        if (form == null && templateId.startsWith("empty-")) {
            int numberOfGroups = Integer.parseInt(templateId.replace("empty-", ""));
            form = NewRideForm.builder(numberOfGroups).get();
        }

        if (form == null) {
            form = NewRideForm.builder(1).get();
        }

        addGlobalValues(principal, model, "Administration - Nouveau ride");
        model.addAttribute("formdata", form);
        model.addAttribute("published", false);
        return "admin_rides_new";

    }

    @GetMapping(value = "/{rideId}")
    public String editRide(@PathVariable("rideId") String rideId,
                           Principal principal,
                           Model model) {

        Optional<Ride> optionalRide = rideService.get(rideId);
        if (optionalRide.isEmpty()) {
            return "redirect:/admin/rides";
        }

        Ride ride = optionalRide.get();

        NewRideForm form = NewRideForm.builder(ride.getGroups().size())
                .withId(ride.getId())
                .withDate(ride.getDate())
                .withDescription(ride.getDescription())
                .withType(ride.getType())
                .withPublishedAt(ride.getPublishedAt())
                .withTitle(ride.getTitle())
                .withGroups(ride.getGroups(), mapService)
                .get();

        addGlobalValues(principal, model, "Administration - Modifier le ride");
        model.addAttribute("formdata", form);
        model.addAttribute("published", ride.getPublishedStatus().equals(PublishedStatus.PUBLISHED));
        return "admin_rides_new";

    }

    @PostMapping(value = "/{rideId}")
    public String editRide(@PathVariable("rideId") String rideId,
                           Principal principal,
                           Model model,
                           NewRideForm form) {

        try {

            boolean isNew = rideId.equals("new");

            NewRideForm.NewRideFormParser parser = form.parser();
            Ride target;
            if (!isNew) {
                Optional<Ride> optionalRide = rideService.get(rideId);
                if (optionalRide.isEmpty()) {
                    return "redirect:/admin/rides";
                }
                target = optionalRide.get();
                target.setDate(parser.getDate());
                target.setPublishedAt(parser.getPublishedAt(configurationService.getTimezone()));
                target.setTitle(parser.getTitle());
                target.setDescription(parser.getDescription());
                target.setType(parser.getType());
            } else {
                target = new Ride(parser.getType(), parser.getDate(), parser.getPublishedAt(configurationService.getTimezone()),
                        parser.getTitle(), parser.getDescription(), parser.getFile().isPresent());
            }

            target.clearGroups();
            parser.getGroups(mapService).forEach(target::addGroup);

            if (parser.getFile().isPresent()) {
                target.setImaged(true);
                MultipartFile uploadedFile = parser.getFile().get();
                rideService.saveImage(target.getId(), form.getFile().getInputStream(), uploadedFile.getOriginalFilename());
            }

            rideService.save(target);

            addGlobalValues(principal, model, "Administration - Rides");
            model.addAttribute("rides", rideService.listRides());
            model.addAttribute("templates", rideTemplateService.listTemplates());
            return "admin_rides";


        } catch (Exception e) {
            addGlobalValues(principal, model, "Administration - Modifier le ride");
            model.addAttribute("errors", List.of(e.getMessage()));
            model.addAttribute("formdata", form);
            return "admin_rides_new";
        }

    }


    @GetMapping(value = "/delete/{rideId}")
    public String deleteRide(@PathVariable("rideId") String rideId,
                             Model model) {

        try {
            rideService.delete(rideId);
        } catch (Exception e) {
            model.addAttribute("errors", List.of(e.getMessage()));
        }

        return "redirect:/admin/rides";

    }


}
