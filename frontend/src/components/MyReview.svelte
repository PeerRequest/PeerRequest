<script>
    import { BreadcrumbItem, TableBodyCell, TableBodyRow } from "flowbite-svelte";
    export let myReview;
</script>

<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href="/categories/{myReview.paper.category.id}/{myReview.paper.id}">{myReview.paper.title}</BreadcrumbItem>
    </TableBodyCell>
    <TableBodyCell>
        <BreadcrumbItem href="/categories/{myReview.paper.category.id}">{myReview.paper.category.name}</BreadcrumbItem>
    </TableBodyCell>
</TableBodyRow>