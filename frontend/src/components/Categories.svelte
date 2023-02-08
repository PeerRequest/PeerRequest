<script>
  import {Table, TableBody, TableHead, TableHeadCell} from "flowbite-svelte";
</script>

<Table hoverable={true} shadow={true}>
  <TableHead>
    <TableHeadCell>Conferences</TableHeadCell>
    <TableHeadCell>Type</TableHeadCell>
    <TableHeadCell>Year</TableHeadCell>
    <TableHeadCell>Review Deadline</TableHeadCell>
    <TableHeadCell>
      <span class="sr-only"> Edit </span>
    </TableHeadCell>
  </TableHead>
  <TableBody>
    <slot/>
  </TableBody>
</Table>