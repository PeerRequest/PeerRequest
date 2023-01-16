<script>
  import { Table, TableBody, TableHead, TableHeadCell } from "flowbite-svelte";

  export let category_type;
</script>

<Table noborder={false} shadow={true}>
  <TableHead>
    <TableHeadCell>Title</TableHeadCell>

    {#if category_type === "Internal" }
      <TableHeadCell>Open Review Slots</TableHeadCell>
      <TableHeadCell></TableHeadCell>
    {/if}
  </TableHead>
  <TableBody class="divide-y">
    <slot></slot>
  </TableBody>
</Table>