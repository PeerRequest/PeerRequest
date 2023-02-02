<script>
    import {BreadcrumbItem, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    //import StarRating from 'svelte-star-rating';
    import StarRating from "./StarRating.svelte";

    export let href;
    export let title;

</script>


<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href={href}>{title}</BreadcrumbItem>
    </TableBodyCell>
    <TableBodyCell>
        <StarRating />
    </TableBodyCell>
</TableBodyRow>
