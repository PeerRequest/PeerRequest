<script>
    import {BreadcrumbItem, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import Skeleton from "svelte-skeleton-loader"
    import {onMount} from "svelte";

    export let review ={
        reviewer_id: ""
    };
    export let category = null;
    export let error = null;

    export let reviewer = null;
    export let paper = null;
    export let loading = false;

    export let show_category = false;
    export let show_paper = false;
    export let show_reviewer = false;

    function loadUser() {
        reviewer = null;
        fetch("/api/users/" + review.reviewer_id )
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    reviewer = resp
                }
            })
            .catch(err => console.log(err))
    }

    onMount(() => {
      loadUser()
    })

</script>


<TableBodyRow>
    {#if loading}
        {#each [...Array(5).keys()] as i}
            <TableBodyCell>
                <div>
                    <Skeleton/>
                </div>
            </TableBodyCell>
        {/each}
    {:else }
        <TableBodyCell>
            <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}/reviews/{review.id}" >Review #{review.id}</BreadcrumbItem>
        </TableBodyCell>

        {#if show_reviewer && reviewer !== null}
            <TableBodyCell>
                <BreadcrumbItem>{reviewer.firstName + " " + reviewer.lastName}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if show_paper}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{paper.category_id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if show_category}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}
    {/if}

</TableBodyRow>