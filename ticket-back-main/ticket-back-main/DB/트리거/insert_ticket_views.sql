DELIMITER //

CREATE TRIGGER insert_ticket_views
AFTER INSERT ON TOW.tickets
FOR EACH ROW
BEGIN
    INSERT INTO TOW.ticket_views (ticket_id)
    VALUES (NEW.id);
END //

DELIMITER ;