<script>
  import { BreadcrumbItem, Button, TableBodyCell, TableBodyRow } from "flowbite-svelte";

  export let href;
  export let title;
  export let category;
  export let slots;
  export let show_category = false;
</script>

<TableBodyRow>
  <TableBodyCell>
    <BreadcrumbItem>{title}</BreadcrumbItem>
  </TableBodyCell>
  {#if show_category}
    <TableBodyCell>
      <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem>
    </TableBodyCell>
  {/if}
  {#if category.type === "Internal" }
    <TableBodyCell>{slots}</TableBodyCell>
    <TableBodyCell>
      <Button disabled={slots<=0} href={href} outline size="xs">
        Start Review
      </Button>
    </TableBodyCell>
  {/if}
</TableBodyRow>
