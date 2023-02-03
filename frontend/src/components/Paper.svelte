<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import StarRating from "./StarRating.svelte";

    export let href;
    export let title;
    export let id;
    export let paper;
    export let rating = null;
    export let category = null;
    export let slots = null;

</script>


<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href="/categories/{paper.category.id}/{paper.id}">{paper.title}</BreadcrumbItem>
    </TableBodyCell>

    {#if category !== null}
        <TableBodyCell>
            <BreadcrumbItem
                    href="/categories/{category.id}">{category.name}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

    {#if (slots !== null) && (category.type === "Internal")}
        <TableBodyCell>{slots}</TableBodyCell>
        <TableBodyCell>
            <Button disabled={slots<=0} href={href} outline size="xs">
                Start Review
            </Button>
        </TableBodyCell>
    {/if}

    {#if rating !== null}
        <TableBodyCell>
            <StarRating rating={rating}/>
        </TableBodyCell>
    {/if}


</TableBodyRow>