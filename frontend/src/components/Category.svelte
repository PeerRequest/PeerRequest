<script>
  import { BreadcrumbItem, Button, TableBodyCell, TableBodyRow } from "flowbite-svelte";

  export let category;
</script>

<TableBodyRow>
  <TableBodyCell>
    <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem>
  </TableBodyCell>
  <TableBodyCell>{category.type}</TableBodyCell>
  <TableBodyCell>{category.year}</TableBodyCell>
  <TableBodyCell>{category.deadline}</TableBodyCell>
  <TableBodyCell>
    <Button href="/categories/{category.id}" outline size="xs">Papers</Button>
  </TableBodyCell>
</TableBodyRow>
