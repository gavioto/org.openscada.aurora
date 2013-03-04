-- DROP TABLE openscada_ds;

CREATE TABLE openscada_ds
(
  instance_id                  VARCHAR(255) NOT NULL,
  node_id                      VARCHAR(512) NOT NULL,
  data                         VARCHAR(4000),
  sequence_nr                  INTEGER NOT NULL,
  CONSTRAINT openscada_ds_pkey PRIMARY KEY (instance_id , node_id , sequence_nr )
);

CREATE INDEX openscada_ds_idx_1 ON openscada_ds (instance_id);
CREATE INDEX openscada_ds_idx_2 ON openscada_ds (node_id, instance_id);
CREATE INDEX openscada_ds_idx_3 ON openscada_ds (instance_id, sequence_nr);
CREATE INDEX openscada_ds_idx_4 ON openscada_ds (node_id, instance_id, sequence_nr);

-- ALTER TABLE openscada_ds OWNER TO openscada;
