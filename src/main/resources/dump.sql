COPY (
  select myentity_a0_.id as id1_0_, myentity_a0_.rev as rev2_0_, myentity_a0_.revtype as revtype3_0_, myentity_a0_.created_at as created_4_0_, myentity_a0_.modified_at as modified5_0_, myentity_a0_.name as name6_0_
  from my_entity_aud myentity_a0_
  where myentity_a0_.modified_at <= '2021-10-08 16:23:23.249032'
  	and myentity_a0_.modified_at=(select max(myentity_a1_.modified_at) from my_entity_aud myentity_a1_ where myentity_a1_.modified_at <= '2021-10-08 16:23:23.249032')
  order by myentity_a0_.rev asc
) TO '/tmp/dump.csv' (format csv, delimiter ';');