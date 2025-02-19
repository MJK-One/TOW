DELIMITER //

CREATE TRIGGER set_region BEFORE INSERT ON TOW.tickets
FOR EACH ROW
BEGIN
    IF NEW.address LIKE '서울%' THEN
        SET NEW.region = '서울';
    ELSEIF NEW.address LIKE '경기%' THEN
        SET NEW.region = '경기';
    ELSEIF NEW.address LIKE '인천%' THEN
        SET NEW.region = '인천';
	ELSEIF NEW.address LIKE '강원%' THEN
        SET NEW.region = '강원';
	ELSEIF NEW.address LIKE '충청북도%' THEN
        SET NEW.region = '충청북도';
	ELSEIF NEW.address LIKE '충청남도%' THEN
        SET NEW.region = '충청남도';
	ELSEIF NEW.address LIKE '세종%' THEN
        SET NEW.region = '세종';
	ELSEIF NEW.address LIKE '대전%' THEN
        SET NEW.region = '대전';
	ELSEIF NEW.address LIKE '경상북도%' THEN
        SET NEW.region = '경상북도';
	ELSEIF NEW.address LIKE '경상남도%' THEN
        SET NEW.region = '경상남도';
	ELSEIF NEW.address LIKE '대구%' THEN
        SET NEW.region = '대구';
	ELSEIF NEW.address LIKE '울산%' THEN
        SET NEW.region = '울산';
	ELSEIF NEW.address LIKE '전라북도%' THEN
        SET NEW.region = '전라북도';
	ELSEIF NEW.address LIKE '전라남도%' THEN
        SET NEW.region = '전라남도';
	ELSEIF NEW.address LIKE '부산%' THEN
        SET NEW.region = '부산';
	ELSEIF NEW.address LIKE '광주%' THEN
        SET NEW.region = '광주';
	ELSEIF NEW.address LIKE '제주%' THEN
        SET NEW.region = '제주';
    ELSE
        SET NEW.region = NULL; -- 일치하는 지역이 없을 경우
    END IF;
END //

DELIMITER ;