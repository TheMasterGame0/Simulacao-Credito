-- This file allow to write SQL commands that will be emitted in test and dev.
-- The commands are commented as their support depends of the database
-- insert into myentity (id, field) values(1, 'field-1');
-- insert into myentity (id, field) values(2, 'field-2');
-- insert into myentity (id, field) values(3, 'field-3');
-- alter sequence myentity_seq restart with 4;

CREATE TABLE SIMULACAO (
  NU_SIMULACAO BIGINT AUTO_INCREMENT PRIMARY KEY,
  DT_SIMULACAO TIMESTAMP NOT NULL,
  NU_PRODUTO INT NOT NULL,
  VR_DESEJADO DECIMAL(15,2) NOT NULL,
  PRAZO INT NOT NULL,
  VR_TOTAL_PARCELAS DECIMAL(15,2) NOT NULL
);